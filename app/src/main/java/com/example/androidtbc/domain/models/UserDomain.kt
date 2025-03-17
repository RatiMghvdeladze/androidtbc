package com.example.androidtbc.domain.models

data class UserDomain(
    val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val avatar: String
)