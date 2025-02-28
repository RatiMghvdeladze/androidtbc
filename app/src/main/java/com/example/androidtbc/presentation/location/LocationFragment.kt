package com.example.androidtbc.presentation.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androidtbc.BaseFragment
import com.example.androidtbc.R
import com.example.androidtbc.data.remote.dto.LocationDto
import com.example.androidtbc.databinding.FragmentLocationBinding
import com.example.androidtbc.utils.Resource
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationFragment : BaseFragment<FragmentLocationBinding>(FragmentLocationBinding::inflate) {

    private val viewModel: LocationViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                initializeMapWithLocation()
                Toast.makeText(requireContext(), "Precise location access granted", Toast.LENGTH_SHORT).show()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                initializeMapWithLocation()
                Toast.makeText(requireContext(), "Approximate location access granted", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(requireContext(), "Location permission is required to view the map", Toast.LENGTH_LONG).show()
                setupMapWithoutLocation()
            }
        }
    }

    override fun start() {
        checkLocationPermissions()
        setupBottomSheet()
        setupObservers()
        setupListeners()
    }

    private fun checkLocationPermissions() {
        when {
            hasLocationPermissions(requireContext()) -> {
                initializeMapWithLocation()
            }
            else -> {
                requestLocationPermissions()
            }
        }
    }

    private fun hasLocationPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    private fun initializeMapWithLocation() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            googleMap = map

            try {
                if (hasLocationPermissions(requireContext())) {
                    map.isMyLocationEnabled = true
                    viewModel.setLocationPermissionGranted(true)
                }
            } catch (e: SecurityException) {
                Toast.makeText(requireContext(), "Error enabling location on map", Toast.LENGTH_SHORT).show()
            }

            map.setOnMapClickListener {
                viewModel.clearSelectedLocation()
                hideBottomSheet()
            }

            viewModel.getLocations()

            Toast.makeText(requireContext(), "Map initialized, fetching locations...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLocationState(state: Resource<List<LocationDto>>) {
        when (state) {
            is Resource.Idle -> {
                binding.progressBar.visibility = View.GONE
            }
            is Resource.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Loading locations...", Toast.LENGTH_SHORT).show()
            }
            is Resource.Success -> {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Found ${state.data.size} locations", Toast.LENGTH_SHORT).show()
                if (state.data.isNotEmpty()) {
                    addMarkersToMap(state.data)
                } else {
                    Toast.makeText(requireContext(), "No locations available to display", Toast.LENGTH_LONG).show()
                }
            }
            is Resource.Error -> {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${state.errorMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addMarkersToMap(locations: List<LocationDto>) {
        if (googleMap == null) {
            Toast.makeText(requireContext(), "Map not initialized yet", Toast.LENGTH_SHORT).show()
            return
        }

        googleMap?.clear()
        var markersAdded = 0

        locations.forEach { location ->
            try {
                val position = LatLng(location.latitude, location.longitude)
                val marker = googleMap?.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(location.title)
                        .snippet(location.address)
                )
                marker?.tag = location
                markersAdded++
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error adding marker: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        Toast.makeText(requireContext(), "Added $markersAdded markers to map", Toast.LENGTH_SHORT).show()

        googleMap?.setOnMarkerClickListener { marker ->
            val location = marker.tag as? LocationDto ?: return@setOnMarkerClickListener false
            viewModel.selectLocation(location)

            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude),
                    15f
                )
            )

            true
        }

        if (markersAdded > 0) {
            zoomToFitAllMarkers()
        }
    }

    private fun setupMapWithoutLocation() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            googleMap = map

            map.setOnMapClickListener {
                viewModel.clearSelectedLocation()
                hideBottomSheet()
            }

            viewModel.getLocations()
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.root)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.locationsState.collect { state ->
                        handleLocationState(state)
                    }
                }

                launch {
                    viewModel.selectedLocation.collect { location ->
                        location?.let {
                            showLocationDetails(it)
                        }
                    }
                }
            }
        }
    }



    private fun setupListeners() {
        binding.fabZoom.setOnClickListener {
            zoomToFitAllMarkers()
        }
    }

    private fun zoomToFitAllMarkers() {
        val state = viewModel.locationsState.value
        if (state !is Resource.Success || state.data.isEmpty()) return

        val locations = state.data
        val builder = LatLngBounds.Builder()
        locations.forEach { location ->
            builder.include(LatLng(location.latitude, location.longitude))
        }

        val bounds = builder.build()
        val padding = resources.getDimensionPixelSize(R.dimen.map_padding)

        googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
    }

    private fun showLocationDetails(location: LocationDto) {
        with(binding.bottomSheet) {
            tvLocationTitle.text = location.title
            tvLocationAddress.text = location.address
            tvLocationCoordinates.text =
                getString(R.string.location_coordinates, location.latitude, location.longitude)

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun hideBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }
}