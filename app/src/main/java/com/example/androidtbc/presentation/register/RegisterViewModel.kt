package com.example.androidtbc.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.remote.api.RetrofitClient
import com.example.androidtbc.data.remote.dto.RegisterResponseDTO
import com.example.androidtbc.domain.model.RegisterRawData
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

class RegisterViewModel : ViewModel() {
    private val _registrationState = MutableStateFlow<Resource<String>>(Resource.Idle)
    val registrationState: StateFlow<Resource<String>> = _registrationState.asStateFlow()

    private val validator = Validator()

    fun register(email: String, password: String, repeatPassword: String) {
        if (!validateInputs(email, password, repeatPassword)) return

        viewModelScope.launch {
            registerFlow(email, password)
                .collect { state ->
                    _registrationState.value = state
                }
        }
    }

    private fun registerFlow(email: String, password: String) = flow {
        emit(Resource.Loading)

        val response = handleHttpRequest {
            RetrofitClient.authService.registerUser(RegisterRawData(email, password))
        }

        when (response) {
            is Resource.Success -> {
                val result = handleRegistrationSuccess(response.data, email)
                emit(result)
            }
            is Resource.Error -> {
                val errorMessage = response.errorMessage.ifEmpty {
                    "Registration failed. Please try again."
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

    private fun handleRegistrationSuccess(response: RegisterResponseDTO, email: String): Resource<String> {
        return if (response.token.isEmpty()) {
            Resource.Error("Registration failed. This email might already be registered.")
        } else {
            Resource.Success(email)
        }
    }

    private fun validateInputs(email: String, password: String, repeatPassword: String): Boolean {
        return when {
            email.isEmpty() || password.isEmpty() -> {
                _registrationState.value = Resource.Error("Please fill in all required fields")
                false
            }
            password != repeatPassword -> {
                _registrationState.value = Resource.Error("Passwords do not match")
                false
            }
            !validator.validateEmail(email) -> {
                _registrationState.value = Resource.Error("Please enter a valid email address")
                false
            }
            !validator.validatePassword(password) -> {
                _registrationState.value = Resource.Error(
                    "Password must contain at least 6 characters"
                )
                false
            }
            else -> true
        }
    }
}