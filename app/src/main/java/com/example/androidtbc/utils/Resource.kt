package com.example.androidtbc.utils

import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(
        val token: String,
        val email: String? = null,
        val message: String? = null
    ) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val errorMessage: String) : Resource<Nothing>()
}

suspend fun <T> handleHttpRequest(
    apiCall: suspend () -> Response<T>
): Resource<T> {
    return try {
        Resource.Loading
        val response = apiCall.invoke()
        if (response.isSuccessful) {
            response.body()?.let {
                Resource.Success(data = it)
            } ?: Resource.Error("Response body is null")
        } else {
            Resource.Error(response.message())
        }
    } catch (throwable: Throwable) {
        when (throwable) {
            is IOException -> Resource.Error("No internet connection")
            is HttpException -> Resource.Error("Server error: ${throwable.code()}")
            else -> Resource.Error("Unknown error occurred")
        }
    }
}