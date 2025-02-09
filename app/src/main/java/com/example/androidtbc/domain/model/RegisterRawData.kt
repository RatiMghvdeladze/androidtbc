package com.example.androidtbc.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRawData(
    val email: String,
    val password: String,
)
