package com.example.androidtbc.presentation.login

sealed class LoginIntent {
    data class LoginUser(val email: String, val password: String, val rememberMe: Boolean) : LoginIntent()
    data object ClearValidationErrors : LoginIntent()
    data object CheckUserSession : LoginIntent()
}

data class LoginViewState(
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val loginSuccess: String? = null,
    val errorMessage: String? = null,
    val savedUserEmail: String? = null
)

sealed class LoginEvent {
    data class ShowSnackbar(val message: String) : LoginEvent()
    data class NavigateToHome(val email: String) : LoginEvent()
}