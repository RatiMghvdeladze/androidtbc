package com.example.androidtbc.data.utils

import android.util.Log
import com.example.androidtbc.domain.common.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okio.IOException
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class ApiHelper @Inject constructor() {
    // Use IO dispatcher for network operations
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    // Add this logging in ApiHelper
    fun <T> handleHttpRequest(apiCall: suspend () -> Response<T>): Flow<Resource<T>> =
        flow {
            emit(Resource.Loading(isLoading = true))
            try {
                val response = apiCall.invoke()
                Log.d("ApiHelper", "API response: isSuccessful=${response.isSuccessful}, code=${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("ApiHelper", "API response body is null: ${body == null}")

                    emit(value = body?.let {
                        Resource.Success(data = it)
                    } ?: Resource.Error(errorMessage = "Empty response body"))
                } else {
                    emit(Resource.Error(errorMessage = response.message() ?: "Unknown error"))
                }
            } catch (throwable: Throwable) {
                Log.e("ApiHelper", "API call failed with error: ${throwable.message}")
                val errorMessage = when (throwable) {
                    is IOException -> "Network error: Check your internet connection"
                    is HttpException -> throwable.message ?: "HTTP error occurred"
                    is IllegalStateException -> throwable.message ?: "Application error"
                    else -> throwable.message ?: "Unknown error occurred"
                }
                emit(Resource.Error(errorMessage = errorMessage))
            } finally {
                emit(Resource.Loading(isLoading = false))
            }
        }.flowOn(ioDispatcher)
}