package com.example.androidtbc.presentation.register

sealed class RegisterIntent {
    data class RegisterUser(val email: String, val password: String, val repeatPassword: String) : RegisterIntent()
    data object ClearValidationErrors : RegisterIntent()
}

data class RegisterState(
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val repeatPasswordError: String? = null,
    val registrationSuccess: String? = null,
    val errorMessage: String? = null
)

sealed class RegisterEvent {
    data class ShowSnackbar(val message: String) : RegisterEvent()
    data class NavigateBack(val email: String, val password: String) : RegisterEvent()
}