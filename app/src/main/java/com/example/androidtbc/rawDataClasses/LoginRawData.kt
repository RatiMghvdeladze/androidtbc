package com.example.androidtbc.rawDataClasses

import kotlinx.serialization.Serializable

@Serializable
data class LoginRawData(
    val email: String,
    val password: String,
)
