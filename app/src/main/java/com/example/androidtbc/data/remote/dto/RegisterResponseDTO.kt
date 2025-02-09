package com.example.androidtbc.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseDTO(
    val id: Int,
    val token: String
)