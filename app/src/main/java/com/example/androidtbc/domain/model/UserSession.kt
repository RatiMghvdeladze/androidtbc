package com.example.androidtbc.domain.model

data class UserSession(
    val email: String,
    val token: String,
    val isActive: Boolean
)