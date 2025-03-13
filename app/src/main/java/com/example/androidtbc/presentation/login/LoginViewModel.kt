package com.example.androidtbc.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.usecase.auth.GetUserSessionUseCase
import com.example.androidtbc.domain.usecase.auth.LoginUseCase
import com.example.androidtbc.domain.usecase.validation.ValidatePasswordUseCase
import com.example.androidtbc.domain.validation.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getUserSessionUseCase: GetUserSessionUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) : ViewModel() {
    private val _viewState = MutableStateFlow(LoginViewState())
    val viewState: StateFlow<LoginViewState> = _viewState.asStateFlow()

    private val _eventChannel = Channel<LoginEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun processIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.LoginUser -> login(intent.email, intent.password, intent.rememberMe)
            is LoginIntent.ClearValidationErrors -> clearValidationErrors()
            is LoginIntent.CheckUserSession -> checkUserSession()
        }
    }

    private fun checkUserSession() {
        viewModelScope.launch {
            getUserSessionUseCase.getUserEmail().collect { email ->
                if (!email.isNullOrEmpty()) {
                    _viewState.value = _viewState.value.copy(savedUserEmail = email)
                    _eventChannel.send(LoginEvent.NavigateToHome(email))
                }
            }
        }
    }

    private fun clearValidationErrors() {
        _viewState.value = _viewState.value.copy(
            emailError = null,
            passwordError = null,
            errorMessage = null
        )
    }

    private fun login(email: String, password: String, rememberMe: Boolean) {
        var hasErrors = false

        if (email.isEmpty()) {
            _viewState.value = _viewState.value.copy(emailError = "Email cannot be empty")
            hasErrors = true
        } else if (!isValidEmail(email)) {
            _viewState.value = _viewState.value.copy(emailError = "Please enter a valid email address")
            hasErrors = true
        }

        when (val passwordValidation = validatePasswordUseCase(password)) {
            is ValidationResult.Error -> {
                _viewState.value = _viewState.value.copy(passwordError = passwordValidation.message)
                hasErrors = true
            }
            else -> {}
        }

        if (hasErrors) {
            return
        }

        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(isLoading = true, errorMessage = null)

            loginUseCase(email, password, rememberMe).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _viewState.value = _viewState.value.copy(
                            isLoading = false,
                            loginSuccess = result.data
                        )
                        _eventChannel.send(LoginEvent.ShowSnackbar("Successfully Logged In!"))
                        _eventChannel.send(LoginEvent.NavigateToHome(email))
                    }
                    is Resource.Error -> {
                        _viewState.value = _viewState.value.copy(
                            isLoading = false,
                            errorMessage = result.errorMessage
                        )
                        _eventChannel.send(LoginEvent.ShowSnackbar(result.errorMessage))
                    }
                    is Resource.Loading -> {
                        _viewState.value = _viewState.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Idle -> {
                        _viewState.value = _viewState.value.copy(isLoading = false)
                    }
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}