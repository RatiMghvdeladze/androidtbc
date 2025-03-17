package com.example.androidtbc.presentation.register

data class RegisterState(
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val repeatPasswordError: String? = null,
    val registrationSuccess: String? = null,
    val errorMessage: String? = null
)

sealed class RegisterEvent {
    data class RegisterUser(val email: String, val password: String, val repeatPassword: String) : RegisterEvent()
    data object ClearValidationErrors : RegisterEvent()
    data class ShowSnackbar(val message: String) : RegisterEvent()
    data class NavigateBack(val email: String, val password: String) : RegisterEvent()
}