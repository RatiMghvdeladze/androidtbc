package com.example.androidtbc.presentation.login

data class LoginState(
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val loginSuccess: String? = null,
    val errorMessage: String? = null,
    val savedUserEmail: String? = null
)

sealed class LoginEvent {
    data class LoginUser(val email: String, val password: String, val rememberMe: Boolean) : LoginEvent()
    data object ClearValidationErrors : LoginEvent()
    data object CheckUserSession : LoginEvent()
    data class ShowSnackbar(val message: String) : LoginEvent()
    data class NavigateToHome(val email: String) : LoginEvent()
}