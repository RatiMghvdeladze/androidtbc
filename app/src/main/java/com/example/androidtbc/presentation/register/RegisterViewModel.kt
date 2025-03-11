package com.example.androidtbc.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.domain.usecase.auth.RegisterUseCase
import com.example.androidtbc.domain.usecase.validation.ValidatePasswordUseCase
import com.example.androidtbc.domain.usecase.validation.ValidateRepeatedPasswordUseCase
import com.example.androidtbc.domain.validation.ValidationResult
import com.example.androidtbc.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateRepeatedPasswordUseCase: ValidateRepeatedPasswordUseCase
) : ViewModel() {
    private val _registrationState = MutableStateFlow<Resource<String>>(Resource.Idle)
    val registrationState: StateFlow<Resource<String>> = _registrationState.asStateFlow()

    fun register(email: String, password: String, repeatPassword: String) {
       if (email.isEmpty()) {
            _registrationState.value = Resource.Error("Email cannot be empty")
            return
        }

       when (val passwordValidation = validatePasswordUseCase(password)) {
            is ValidationResult.Error -> {
                _registrationState.value = Resource.Error(passwordValidation.message)
                return
            }
            is ValidationResult.Success -> {
               when (val repeatedPasswordValidation = validateRepeatedPasswordUseCase(password, repeatPassword)) {
                    is ValidationResult.Error -> {
                        _registrationState.value = Resource.Error(repeatedPasswordValidation.message)
                        return
                    }
                    is ValidationResult.Success -> {
                        viewModelScope.launch {
                            _registrationState.value = Resource.Loading
                            registerUseCase(email, password, repeatPassword)
                                .collect { result ->
                                    _registrationState.value = result
                                }
                        }
                    }
                }
            }
        }
    }
}