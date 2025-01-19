package com.example.androidtbc

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseDTO(
    val id: Int?,
    val token: String?
)
