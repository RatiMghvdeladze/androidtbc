package com.example.androidtbc.presentation.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androidtbc.BaseFragment
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentLocationBinding
import com.example.androidtbc.presentation.location.model.LocationUi
import com.example.androidtbc.presentation.location.utils.Resource
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationFragment : BaseFragment<FragmentLocationBinding>(FragmentLocationBinding::inflate) {

    private val viewModel: LocationViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private lateinit var clusterManager: ClusterManager<LocationClusterItem>

    override fun start() {
        observers()
        setUpListeners()
        checkLocationPermissions()
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Toast.makeText(requireContext(), "Location services enabled", Toast.LENGTH_SHORT).show()
                initializeMap(true)
            }
            else -> {
                showPermissionRationale {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", requireContext().packageName, null)
                    }
                    startActivity(intent)
                }
            }
        }
    }


    private fun checkLocationPermissions() {
        if (hasLocationPermissions(requireContext())) {
            initializeMap(true)
        } else {
            requestPermissions()
        }
    }

    private fun hasLocationPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            showPermissionRationale {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showPermissionRationale(onRationaleShown: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.location_permission_needed))
            .setMessage(getString(R.string.this_app_needs_location_permissions_to_function_properly_we_use_your_location_to_provide_accurate_location_based_services))
            .setPositiveButton(getString(R.string.ok)) { _, _ -> onRationaleShown() }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun initializeMap(locationEnabled: Boolean) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            googleMap = map
            setupClusterManager()

            if (locationEnabled && hasLocationPermissions(requireContext())) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    googleMap?.uiSettings?.isMyLocationButtonEnabled = false
                    googleMap?.isMyLocationEnabled = true
                    viewModel.setLocationPermissionGranted(true)
                } else {
                    viewModel.setLocationPermissionGranted(false)
                }
            } else {
                viewModel.setLocationPermissionGranted(false)
            }

            map.setOnMapClickListener {
                viewModel.clearSelectedLocation()
                hideBottomSheet()
            }

            viewModel.getLocations()
        }
    }

    private fun setupClusterManager() {
        googleMap?.let { map ->
            clusterManager = ClusterManager(requireContext(), map)

            val renderer = ClusterRenderer(requireContext(), map, clusterManager)
            clusterManager.renderer = renderer

            clusterManager.setOnClusterClickListener { cluster ->
                val builder = LatLngBounds.Builder()
                cluster.items.forEach { builder.include(it.position) }

                val bounds = builder.build()
                val padding = resources.getDimensionPixelSize(R.dimen.map_padding)
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
                true
            }

            clusterManager.setOnClusterItemClickListener { item ->
                hideBottomSheet()

                viewModel.selectLocation(item.locationUi)
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(item.locationUi.latitude, item.locationUi.longitude),
                        15f
                    )
                )
                true
            }

            map.setOnCameraIdleListener(clusterManager)
        }
    }

    private fun handleLocationState(state: Resource<List<LocationUi>>) {
        binding.progressBar.visibility = if (state is Resource.Loading) View.VISIBLE else View.GONE

        when (state) {
            is Resource.Success -> {
                if (state.data.isNotEmpty()) {
                    addMarkersToClusterManager(state.data)
                }
            }
            is Resource.Error -> {
                Toast.makeText(requireContext(), "Error: ${state.errorMessage}", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    private fun addMarkersToClusterManager(locations: List<LocationUi>) {
        if (googleMap == null || !::clusterManager.isInitialized) return

        clusterManager.clearItems()
        locations.forEach { locationUi ->
            clusterManager.addItem(LocationClusterItem(locationUi))
        }
        clusterManager.cluster()

        if (locations.isNotEmpty()) {
            zoomToAllMarkers(locations)
        }
    }

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.locationsState.collect { state ->
                        handleLocationState(state)
                    }
                }

                launch {
                    viewModel.selectedLocation.collect { location ->
                        location?.let { showLocationDetails(it) }
                    }
                }
            }
        }
    }

    private fun setUpListeners() {
        binding.fabZoom.setOnClickListener {
            if (hasLocationPermissions(requireContext())) {
                googleMap?.let { map ->
                    map.animateCamera(CameraUpdateFactory.zoomTo(15f))

                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            location?.let {
                                val userLatLng = LatLng(it.latitude, it.longitude)
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                            }
                        }
                    } else {
                        requestPermissions()
                    }
                }
            } else {
                requestPermissions()
            }
        }
    }

    private fun zoomToAllMarkers(locations: List<LocationUi>) {
        val builder = LatLngBounds.Builder()
        locations.forEach {
            builder.include(LatLng(it.latitude, it.longitude))
        }

        val bounds = builder.build()
        val padding = resources.getDimensionPixelSize(R.dimen.map_padding)

        googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
    }

    private fun showLocationDetails(location: LocationUi) {
        hideBottomSheet()

        val bottomSheetFragment = LocationBottomSheetFragment.newInstance(location)
        bottomSheetFragment.show(childFragmentManager, "LocationBottomSheet")
    }

    private fun hideBottomSheet() {
        childFragmentManager.fragments.forEach { fragment ->
            if (fragment is LocationBottomSheetFragment) {
                fragment.dismiss()
            }
        }
    }
}