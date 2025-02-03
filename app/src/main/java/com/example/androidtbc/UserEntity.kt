package com.example.androidtbc

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String?,
    val activationStatus: Int
)
