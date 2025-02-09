package com.example.androidtbc.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDTO(
    val token: String
)
