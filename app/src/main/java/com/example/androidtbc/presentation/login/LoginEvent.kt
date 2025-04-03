package com.example.androidtbc.presentation.login

sealed class LoginEvent {
    data class LoginUser(val email: String, val password: String, val rememberMe: Boolean) : LoginEvent()
    data object ClearValidationErrors : LoginEvent()
    data object CheckUserSession : LoginEvent()
    data class ShowSnackbar(val message: String) : LoginEvent()
    data class NavigateToHome(val email: String) : LoginEvent()
}