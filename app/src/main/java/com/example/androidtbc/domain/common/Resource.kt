package com.example.androidtbc.domain.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val errorMessage: String) : Resource<Nothing>()
    data class Loading(val isLoading: Boolean) : Resource<Nothing>()
    data object Idle : Resource<Nothing>()
}

fun <DTO, DOMAIN> Flow<Resource<DTO>>.mapResource(mapper: (DTO) -> DOMAIN): Flow<Resource<DOMAIN>> {
    return map { resource ->
        when (resource) {
            is Resource.Success -> {
                Resource.Success(data = mapper(resource.data))
            }
            is Resource.Error -> {
                Resource.Error(errorMessage = resource.errorMessage)
            }
            is Resource.Loading -> {
                Resource.Loading(isLoading = resource.isLoading)
            }
            Resource.Idle -> {
                Resource.Idle
            }
        }
    }
}