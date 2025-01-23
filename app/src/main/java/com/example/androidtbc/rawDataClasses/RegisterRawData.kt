package com.example.androidtbc.rawDataClasses

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRawData(
    val email: String,
    val password: String,
)
