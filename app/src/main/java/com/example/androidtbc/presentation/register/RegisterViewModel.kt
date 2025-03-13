// Replace your existing file at:
// com/example/androidtbc/presentation/register/RegisterViewModel.kt

package com.example.androidtbc.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.usecase.auth.RegisterUseCase
import com.example.androidtbc.domain.usecase.validation.ValidatePasswordUseCase
import com.example.androidtbc.domain.usecase.validation.ValidateRepeatedPasswordUseCase
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
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateRepeatedPasswordUseCase: ValidateRepeatedPasswordUseCase
) : ViewModel() {
    private val _viewState = MutableStateFlow(RegisterViewState())
    val viewState: StateFlow<RegisterViewState> = _viewState.asStateFlow()

    private val _eventChannel = Channel<RegisterEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun processIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.RegisterUser -> register(intent.email, intent.password, intent.repeatPassword)
            is RegisterIntent.ClearValidationErrors -> clearValidationErrors()
        }
    }

    private fun clearValidationErrors() {
        _viewState.value = _viewState.value.copy(
            emailError = null,
            passwordError = null,
            repeatPasswordError = null,
            errorMessage = null
        )
    }

    private fun register(email: String, password: String, repeatPassword: String) {
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
            else -> {
                when (val repeatedPasswordValidation = validateRepeatedPasswordUseCase(password, repeatPassword)) {
                    is ValidationResult.Error -> {
                        _viewState.value = _viewState.value.copy(repeatPasswordError = repeatedPasswordValidation.message)
                        hasErrors = true
                    }
                    else -> {}
                }
            }
        }

        if (hasErrors) {
            return
        }

        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(isLoading = true, errorMessage = null)

            registerUseCase(email, password, repeatPassword).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _viewState.value = _viewState.value.copy(
                            isLoading = false,
                            registrationSuccess = result.data
                        )
                        _eventChannel.send(RegisterEvent.ShowSnackbar("Registration successful!"))
                        _eventChannel.send(RegisterEvent.NavigateBack(email, password))
                    }
                    is Resource.Error -> {
                        _viewState.value = _viewState.value.copy(
                            isLoading = false,
                            errorMessage = result.errorMessage
                        )
                        _eventChannel.send(RegisterEvent.ShowSnackbar(result.errorMessage))
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