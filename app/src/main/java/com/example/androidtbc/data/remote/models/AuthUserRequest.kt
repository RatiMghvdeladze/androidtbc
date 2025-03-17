package com.example.androidtbc.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthUserRequest(
    val email: String,
    val password: String
)