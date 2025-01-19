package com.example.androidtbc

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDTO(
    val token: String?
)
