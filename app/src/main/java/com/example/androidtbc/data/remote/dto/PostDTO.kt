package com.example.androidtbc.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDTO(
    val id: Int,
    val images: List<String>?,
    val title: String,
    val comments: Int,
    val likes: Int,
    @SerialName("share_content")
    val shareContent: String,
    val owner: OwnerDTO
)

@Serializable
data class OwnerDTO(
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val profile: String?,
    @SerialName("post_date")
    val postDate: Long
)