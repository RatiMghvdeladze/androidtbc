package com.example.androidtbc

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRawData(
    val email: String,
    val password: String
)
