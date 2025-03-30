package com.example.androidtbc.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ValidationResponseDto(
    val status: String
)