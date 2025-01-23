package com.example.androidtbc.responseDtoClasses

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDTO(
    val token: String
)
