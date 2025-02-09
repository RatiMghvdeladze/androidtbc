package com.example.androidtbc.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRawData(
    val email: String,
    val password: String,
)
