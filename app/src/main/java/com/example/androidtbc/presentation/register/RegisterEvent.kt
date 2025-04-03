package com.example.androidtbc.presentation.register

sealed class RegisterEvent {
    data class RegisterUser(val email: String, val password: String, val repeatPassword: String) : RegisterEvent()
    data object ClearValidationErrors : RegisterEvent()
    data class ShowSnackbar(val message: String) : RegisterEvent()
    data class NavigateBack(val email: String, val password: String) : RegisterEvent()
}