package com.example.androidtbc

import androidx.room.PrimaryKey

data class User(
    @PrimaryKey val id: Int,
    val name: String,
    val avatar: String?,
    val activationStatus: Int
)
