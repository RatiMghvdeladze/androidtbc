package com.example.androidtbc.domain.models

data class UserSessionDomain(
    val email: String,
    val token: String,
    val isActive: Boolean
)