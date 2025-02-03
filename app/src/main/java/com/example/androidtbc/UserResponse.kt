package com.example.androidtbc

data class UserResponse(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val activation_status: Int
)