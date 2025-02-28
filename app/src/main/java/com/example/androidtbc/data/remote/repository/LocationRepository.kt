package com.example.androidtbc.data.remote.repository

import com.example.androidtbc.data.remote.api.ApiService
import com.example.androidtbc.data.remote.dto.LocationDto
import com.example.androidtbc.presentation.location.utils.Resource
import com.example.androidtbc.presentation.location.utils.handleHttpRequest
import javax.inject.Inject

interface LocationRepository {
    suspend fun getLocations(): Resource<List<LocationDto>>
}


class LocationRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : LocationRepository {

    override suspend fun getLocations(): Resource<List<LocationDto>> {
        return handleHttpRequest {
            apiService.getLocations()
        }
    }
}
