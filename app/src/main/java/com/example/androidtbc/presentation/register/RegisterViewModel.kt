package com.example.androidtbc.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<RegisterEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.RegisterUser -> register(event.email, event.password, event.repeatPassword)
            is RegisterEvent.ClearValidationErrors -> clearValidationErrors()

            is RegisterEvent.ShowSnackbar, is RegisterEvent.NavigateBack -> {}
        }
    }

    private fun clearValidationErrors() {
        _state.value = _state.value.copy(
            emailError = null,
            passwordError = null,
            repeatPasswordError = null,
            errorMessage = null
        )
    }

    private fun register(email: String, password: String, repeatPassword: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            registerUseCase(email, password, repeatPassword).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            registrationSuccess = result.data
                        )
                        _eventChannel.send(RegisterEvent.ShowSnackbar("Successfully Registered!"))
                        _eventChannel.send(RegisterEvent.NavigateBack(email, password))
                    }
                    is Resource.Error -> {
                        val errorMessage = result.errorMessage.ifEmpty {
                            "An error occurred during registration"
                        }

                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = errorMessage
                        )

                        when {
                            errorMessage.contains("email", ignoreCase = true) -> {
                                _state.value = _state.value.copy(emailError = errorMessage)
                            }
                            errorMessage.contains("password") && errorMessage.contains("match", ignoreCase = true) -> {
                                _state.value = _state.value.copy(repeatPasswordError = errorMessage)
                            }
                            errorMessage.contains("password", ignoreCase = true) -> {
                                _state.value = _state.value.copy(passwordError = errorMessage)
                            }
                        }

                        _eventChannel.send(RegisterEvent.ShowSnackbar(errorMessage))
                    }
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = result.isLoading)
                    }
                }
            }
        }
    }
}