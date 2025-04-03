package com.example.androidtbc.presentation.register

data class RegisterState(
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val repeatPasswordError: String? = null,
    val registrationSuccess: String? = null,
    val errorMessage: String? = null
)