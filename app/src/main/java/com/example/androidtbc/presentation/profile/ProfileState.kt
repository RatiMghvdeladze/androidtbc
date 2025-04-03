package com.example.androidtbc.presentation.profile

data class ProfileState(
    val isLoading: Boolean = false,
    val userEmail: String? = null,
    val isSessionActive: Boolean = true,
    val errorMessage: String? = null,
    val isEmpty: Boolean = false
)