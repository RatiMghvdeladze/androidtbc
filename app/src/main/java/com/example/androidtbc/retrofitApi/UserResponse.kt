package com.example.androidtbc.retrofitApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int,
    val avatar: String?,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val about: String?,
    @SerialName("activation_status")
    val activationStatus: Double
)