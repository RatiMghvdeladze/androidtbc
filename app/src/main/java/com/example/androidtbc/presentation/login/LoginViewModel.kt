package com.example.androidtbc.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.local.LocalDataStore
import com.example.androidtbc.data.remote.api.RetrofitClient
import com.example.androidtbc.data.remote.dto.LoginResponseDTO
import com.example.androidtbc.domain.model.LoginRawData
import com.example.androidtbc.utils.AuthState
import com.example.androidtbc.utils.Resource
import com.example.androidtbc.utils.Validator
import com.example.androidtbc.utils.handleHttpRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val dataStore: LocalDataStore) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private val validator = Validator()

    fun login(email: String, password: String, rememberMe: Boolean) {
        if (!validateInputs(email, password)) return

        viewModelScope.launch(Dispatchers.IO) {
            _authState.value = AuthState.Loading

            try {
                when (val response = handleHttpRequest {
                    RetrofitClient.authService.loginUser(LoginRawData(email, password))
                }) {
                    is Resource.Success -> handleLoginSuccess(response.data, email, rememberMe)
                    is Resource.Error -> {
                        val errorMessage = response.errorMessage.ifEmpty {
                            "Failed to login. Please check your credentials and try again."
                        }
                        _authState.value = AuthState.Error(errorMessage)
                    }
                    is Resource.Loading -> Unit
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "An unexpected error occurred. Please try again."
                )
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        return when {
            email.isEmpty() || password.isEmpty() -> {
                _authState.value = AuthState.Error("Please fill in both email and password fields")
                false
            }
            !validator.validateEmail(email) -> {
                _authState.value = AuthState.Error("Please enter a valid email address")
                false
            }
            !validator.validatePassword(password) -> {
                _authState.value = AuthState.Error(
                    "Password must contain at least 6 characters"
                )
                false
            }
            else -> true
        }
    }

    private suspend fun handleLoginSuccess(
        response: LoginResponseDTO,
        email: String,
        rememberMe: Boolean
    ) {
        if (response.token.isEmpty()) {
            _authState.value = AuthState.Error(
                "Invalid credentials. Please check your email and password."
            )
            return
        }

        if (rememberMe) {
            saveEmail(email)
        }
        _authState.value = AuthState.Success(
            token = response.token,
            email = email,
            message = "Successfully Logged In!"
        )
    }


    private suspend fun saveEmail(email: String) {
        dataStore.saveEmail(email)
    }

    fun getEmail() = dataStore.getEmail()
}