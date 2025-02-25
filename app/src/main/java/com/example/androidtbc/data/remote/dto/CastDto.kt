package com.example.androidtbc.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CastDto(
    val cast: List<CastMemberDto>
)
@Serializable
data class CastMemberDto(
    @SerialName("id")
    val id: Int,
    val name: String,
    val character: String,
    @SerialName("profile_path")
    val profilePath: String?,
    val gender: Int? = null,
    val popularity: Double? = null
)