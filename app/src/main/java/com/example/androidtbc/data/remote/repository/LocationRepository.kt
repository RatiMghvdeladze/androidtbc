package com.example.androidtbc.data.remote.repository

import com.example.androidtbc.data.remote.api.ApiService
import com.example.androidtbc.data.remote.dto.LocationDto
import com.example.androidtbc.utils.Resource
import com.example.androidtbc.utils.handleHttpRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getLocations(): Resource<List<LocationDto>> {
        return handleHttpRequest {
            apiService.getLocations()
        }
    }
}