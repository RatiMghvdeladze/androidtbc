package com.example.androidtbc.data.utils

import com.example.androidtbc.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import retrofit2.Response

class ApiHelper {
    fun <T> handleHttpRequest(apiCall: suspend () -> Response<T>): Flow<Resource<T>> =
        flow {
            emit(Resource.Loading(isLoading = true))
            try {
                val response = apiCall.invoke()
                if (response.isSuccessful) {
                    emit(value = response.body()?.let {
                        Resource.Success(data = it)
                    } ?: Resource.Error(errorMessage = "Something error"))
                } else {
                    emit(Resource.Error(errorMessage = response.message()))
                }
            } catch (throwable: Throwable) {
                val errorMessage = when (throwable) {
                    is IOException -> "Network error: Check your internet connection"
                    is HttpException -> throwable.message ?: "HTTP error occurred"
                    is IllegalStateException -> throwable.message ?: "Application error"
                    else -> throwable.message ?: "Unknown error occurred"
                }
                emit(Resource.Error(errorMessage = errorMessage))
            }
        }
}