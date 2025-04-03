package com.example.androidtbc.presentation.login

data class LoginState(
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val loginSuccess: String? = null,
    val errorMessage: String? = null,
    val savedUserEmail: String? = null
)