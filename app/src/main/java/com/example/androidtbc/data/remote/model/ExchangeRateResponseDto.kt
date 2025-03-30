package com.example.androidtbc.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateResponseDto(
    val course: Double,
    val status: String? = null
)