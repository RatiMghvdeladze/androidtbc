package com.example.androidtbc.presentation.model

data class Cast(
    val cast: List<CastMember>
)

data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    val profilePath: String?,
    val gender: Int? = null,
    val popularity: Double? = null
)