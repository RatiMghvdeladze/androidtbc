package com.example.androidtbc.data.remote.dto

// LocationModel.kt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationDto(
    @SerialName("lat")
    val latitude: Double,
    @SerialName("lan")
    val longitude: Double,
    val title: String,
    val address: String
)