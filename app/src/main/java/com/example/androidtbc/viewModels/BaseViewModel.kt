package com.example.androidtbc.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel : ViewModel() {
    private val _error: MutableStateFlow<String?> = MutableStateFlow(null)
    val error = _error.asStateFlow()

    protected fun handleException(exception: Throwable) {
        val errorMessage = when (exception) {
            is java.net.UnknownHostException -> "No internet connection"
            is retrofit2.HttpException -> {
                when (exception.code()) {
                    400 -> "Bad Request"
                    401 -> "Unauthorized access"
                    403 -> "Forbidden"
                    404 -> "Not Found"
                    500 -> "Server Error"
                    503 -> "Service Unavailable"
                    else -> "HTTP Error: ${exception.code()}"
                }
            }
            is java.net.SocketTimeoutException -> "Connection timeout"
            else -> exception.localizedMessage ?: "Unknown error occurred"
        }
        _error.value = errorMessage
    }
}