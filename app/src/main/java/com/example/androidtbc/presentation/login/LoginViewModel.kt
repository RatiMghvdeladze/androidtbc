package com.example.androidtbc.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.usecase.auth.CheckRememberMeUseCase
import com.example.androidtbc.domain.usecase.auth.FetchUserEmailUseCase
import com.example.androidtbc.domain.usecase.auth.LoginUseCase
import com.example.androidtbc.domain.usecase.validation.ValidateEmailUseCase
import com.example.androidtbc.domain.usecase.validation.ValidatePasswordUseCase
import com.example.androidtbc.domain.usecase.validation.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val fetchUserEmailUseCase: FetchUserEmailUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val checkRememberMeUseCase: CheckRememberMeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<LoginEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.LoginUser -> login(event.email, event.password, event.rememberMe)
            is LoginEvent.ClearValidationErrors -> clearValidationErrors()
            is LoginEvent.CheckUserSession -> checkUserSession()
            else -> {}
        }
    }

    fun validateEmail(email: String) {
        val result = validateEmailUseCase(email)
        _state.value = _state.value.copy(
            emailError = when (result) {
                is ValidationResult.Error -> result.errorMessage
                ValidationResult.Success -> null
            }
        )
    }

    fun validatePassword(password: String) {
        val result = validatePasswordUseCase(password)
        _state.value = _state.value.copy(
            passwordError = when (result) {
                is ValidationResult.Error -> result.errorMessage
                ValidationResult.Success -> null
            }
        )
    }

    private fun clearValidationErrors() {
        _state.value = _state.value.copy(
            emailError = null,
            passwordError = null,
            errorMessage = null
        )
    }

    private fun checkUserSession() {
        viewModelScope.launch {
            val rememberMeEnabled = checkRememberMeUseCase()

            if (rememberMeEnabled) {
                fetchUserEmailUseCase().collect { email ->
                    if (!email.isNullOrEmpty()) {
                        _state.value = _state.value.copy(savedUserEmail = email)
                        _eventChannel.send(LoginEvent.NavigateToHome(email))
                    }
                }
            }
        }
    }

    private fun login(email: String, password: String, rememberMe: Boolean) {
        val emailValidation = validateEmailUseCase(email)
        if (emailValidation is ValidationResult.Error) {
            _state.value = _state.value.copy(emailError = emailValidation.errorMessage)
            return
        }

        val passwordValidation = validatePasswordUseCase(password)
        if (passwordValidation is ValidationResult.Error) {
            _state.value = _state.value.copy(passwordError = passwordValidation.errorMessage)
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null
            )

            loginUseCase(email, password, rememberMe).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // Successful login
                        _state.value = _state.value.copy(
                            isLoading = false,
                            loginSuccess = result.data
                        )
                        _eventChannel.send(LoginEvent.ShowSnackbar("Successfully Logged In!"))
                        _eventChannel.send(LoginEvent.NavigateToHome(email))
                    }
                    is Resource.Error -> {
                        val errorMsg = result.errorMessage.ifEmpty {
                            "An error occurred during login"
                        }

                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = errorMsg
                        )
                        _eventChannel.send(LoginEvent.ShowSnackbar(errorMsg))
                    }
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = result.isLoading)
                    }
                }
            }
        }
    }
}