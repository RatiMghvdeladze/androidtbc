package com.example.androidtbc.presentation.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.remote.dto.LocationDto
import com.example.androidtbc.data.remote.repository.LocationRepository
import com.example.androidtbc.utils.Resource
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _locationsState = MutableStateFlow<Resource<List<LocationDto>>>(Resource.Idle)
    val locationsState: StateFlow<Resource<List<LocationDto>>> = _locationsState.asStateFlow()

    private val _selectedLocation = MutableStateFlow<LocationDto?>(null)
    val selectedLocation: StateFlow<LocationDto?> = _selectedLocation.asStateFlow()

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation.asStateFlow()

    private val _locationPermissionGranted = MutableStateFlow(false)
    val locationPermissionGranted: StateFlow<Boolean> = _locationPermissionGranted.asStateFlow()


    fun getLocations() {
        viewModelScope.launch {
            _locationsState.value = Resource.Loading
            val result = locationRepository.getLocations()
            _locationsState.value = result
        }
    }

    fun selectLocation(location: LocationDto) {
        _selectedLocation.value = location
    }

    fun clearSelectedLocation() {
        _selectedLocation.value = null
    }

    fun updateUserLocation(latLng: LatLng) {
        _userLocation.value = latLng
    }

    fun setLocationPermissionGranted(granted: Boolean) {
        _locationPermissionGranted.value = granted
    }

    fun findNearestLocation(): LocationDto? {
        val currentUserLocation = _userLocation.value ?: return null
        val locations = (_locationsState.value as? Resource.Success)?.data ?: return null

        if (locations.isEmpty()) return null

        return locations.minByOrNull { location ->
            val locationLatLng = LatLng(location.latitude, location.longitude)
            calculateDistance(currentUserLocation, locationLatLng)
        }
    }


    private fun calculateDistance(point1: LatLng, point2: LatLng): Float {
        val lat1 = point1.latitude
        val lon1 = point1.longitude
        val lat2 = point2.latitude
        val lon2 = point2.longitude

        val latDiff = Math.toRadians(lat2 - lat1)
        val lonDiff = Math.toRadians(lon2 - lon1)

        val a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(lonDiff / 2) * Math.sin(lonDiff / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        val radius = 6371

        return (radius * c).toFloat()
    }
}