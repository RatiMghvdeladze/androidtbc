package com.example.androidtbc.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.local.LocalDataStore
import com.example.androidtbc.data.remote.api.RetrofitClient
import com.example.androidtbc.data.remote.dto.LoginResponseDTO
import com.example.androidtbc.domain.model.LoginRawData
import com.example.androidtbc.utils.Resource
import com.example.androidtbc.utils.Validator
import com.example.androidtbc.utils.handleHttpRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class LoginViewModel(private val dataStore: LocalDataStore) : ViewModel() {
    private val _loginState = MutableStateFlow<Resource<String>>(Resource.Idle)
    val loginState: StateFlow<Resource<String>> = _loginState.asStateFlow()

    private val validator = Validator()

    fun login(email: String, password: String, rememberMe: Boolean) {
        if (!validateInputs(email, password)) return

        viewModelScope.launch {
            loginFlow(email, password, rememberMe)
                .collect { state ->
                    _loginState.value = state
                }
        }
    }

    private fun loginFlow(email: String, password: String, rememberMe: Boolean) = flow {
        emit(Resource.Loading)

        val response = handleHttpRequest {
            RetrofitClient.authService.loginUser(LoginRawData(email, password))
        }

        when (response) {
            is Resource.Success -> {
                val result = handleLoginSuccess(response.data, email, rememberMe)
                emit(result)
            }
            is Resource.Error -> {
                val errorMessage = response.errorMessage.ifEmpty {
                    "Failed to login. Please check your credentials and try again."
                }
                emit(Resource.Error(errorMessage))
            }
            is Resource.Loading, is Resource.Idle -> Unit
        }
    }.catch { e ->
        emit(
            Resource.Error(
                e.message ?: "An unexpected error occurred. Please try again."
            )
        )
    }.flowOn(Dispatchers.IO)

    private suspend fun handleLoginSuccess(
        response: LoginResponseDTO,
        email: String,
        rememberMe: Boolean
    ): Resource<String> {
        if (response.token.isEmpty()) {
            return Resource.Error(
                "Invalid credentials. Please check your email and password."
            )
        }

        if (rememberMe) {
            saveEmail(email)
        }
        return Resource.Success(email)
    }

    private suspend fun saveEmail(email: String) {
        dataStore.saveEmail(email)
    }

    fun getEmail() = dataStore.getEmail()

    private fun validateInputs(email: String, password: String): Boolean {
        return when {
            email.isEmpty() || password.isEmpty() -> {
                _loginState.value = Resource.Error("Please fill in both email and password fields")
                false
            }
            !validator.validateEmail(email) -> {
                _loginState.value = Resource.Error("Please enter a valid email address")
                false
            }
            !validator.validatePassword(password) -> {
                _loginState.value = Resource.Error(
                    "Password must contain at least 6 characters"
                )
                false
            }
            else -> true
        }
    }
}