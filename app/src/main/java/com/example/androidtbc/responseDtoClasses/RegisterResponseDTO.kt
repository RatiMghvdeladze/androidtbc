package com.example.androidtbc.responseDtoClasses

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseDTO(
    val id: Int?,
    val token: String?
)
