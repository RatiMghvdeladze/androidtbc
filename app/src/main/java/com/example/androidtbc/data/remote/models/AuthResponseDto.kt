package com.example.androidtbc.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto (
    val id: Int?,
    val token: String
)