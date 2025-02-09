package com.example.androidtbc.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.remote.api.RetrofitClient
import com.example.androidtbc.data.remote.dto.RegisterResponseDTO
import com.example.androidtbc.domain.model.RegisterRawData
import com.example.androidtbc.utils.AuthState
import com.example.androidtbc.utils.Resource
import com.example.androidtbc.utils.Validator
import com.example.androidtbc.utils.handleHttpRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private val validator = Validator()

    fun register(email: String, password: String, repeatPassword: String) {
        if (!validateInputs(email, password, repeatPassword)) return

        viewModelScope.launch(Dispatchers.IO) {
            _authState.value = AuthState.Loading

            try {
                when (val response = handleHttpRequest {
                    RetrofitClient.authService.registerUser(RegisterRawData(email, password))
                }) {
                    is Resource.Success -> handleRegistrationSuccess(response.data, email)
                    is Resource.Error -> {
                        val errorMessage = response.errorMessage.ifEmpty {
                            "Registration failed. Please try again."
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

    private fun validateInputs(email: String, password: String, repeatPassword: String): Boolean {
        return when {
            email.isEmpty() || password.isEmpty() -> {
                _authState.value = AuthState.Error("Please fill in all required fields")
                false
            }
            password != repeatPassword -> {
                _authState.value = AuthState.Error("Passwords do not match")
                false
            }
            !validator.validateEmail(email) -> {
                _authState.value = AuthState.Error("Please enter a valid email address")
                false
            }
            !validator.validatePassword(password) -> {
                _authState.value = AuthState.Error(
                    "Password must contain at least 8 characters, including letters and numbers"
                )
                false
            }
            else -> true
        }
    }

    private suspend fun handleRegistrationSuccess(response: RegisterResponseDTO, email: String) {
        if (response.token.isEmpty()) {
            _authState.value = AuthState.Error(
                "Registration failed. This email might already be registered."
            )
            return
        }
        _authState.value = AuthState.Success(
            token = response.token,
            email = email,
            message = "Registration successful! Welcome aboard!"
        )
    }

}