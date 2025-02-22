package com.example.androidtbc.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class StoryDTO(
    val id: Int,
    val cover: String,
    val title: String
)
