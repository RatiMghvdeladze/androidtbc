package com.example.androidtbc.presentation.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.remote.repository.LocationRepository
import com.example.androidtbc.presentation.location.model.LocationUi
import com.example.androidtbc.presentation.location.utils.Resource
import com.example.androidtbc.presentation.location.utils.toUiList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _locationsState = MutableStateFlow<Resource<List<LocationUi>>>(Resource.Idle)
    val locationsState  = _locationsState.asStateFlow()

    private val _selectedLocation = MutableStateFlow<LocationUi?>(null)
    val selectedLocation  = _selectedLocation.asStateFlow()

    private val _locationPermissionGranted = MutableStateFlow(false)
    val locationPermissionGranted = _locationPermissionGranted.asStateFlow()

    fun getLocations() {
        viewModelScope.launch {
            _locationsState.value = Resource.Loading
            when (val result = locationRepository.getLocations()) {
                is Resource.Success -> {
                    _locationsState.value = Resource.Success(result.data.toUiList())
                }
                is Resource.Error -> {
                    _locationsState.value = Resource.Error(result.errorMessage)
                }
                else -> {}
            }
        }
    }

    fun selectLocation(location: LocationUi) {
        _selectedLocation.value = location
    }

    fun clearSelectedLocation() {
        _selectedLocation.value = null
    }

    fun setLocationPermissionGranted(granted: Boolean) {
        _locationPermissionGranted.value = granted
    }
}