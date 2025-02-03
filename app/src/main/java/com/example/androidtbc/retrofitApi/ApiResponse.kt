package com.example.androidtbc.retrofitApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val status: Boolean,
    @SerialName("additional_data")
    val additionalData: String? = null,
    val options: String? = null,
    val permissions: List<String?>,
    val users: List<UserResponse>
)
