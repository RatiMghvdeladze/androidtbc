package com.example.androidtbc.data.mapper

import com.example.androidtbc.data.local.entity.UserEntity
import com.example.androidtbc.data.remote.models.AuthResponseDto
import com.example.androidtbc.data.remote.models.UserResponseDto
import com.example.androidtbc.domain.models.AuthDomain
import com.example.androidtbc.domain.models.UserDomain

fun AuthResponseDto.toDomain() : AuthDomain {
    return AuthDomain(
        id = id,
        token = token
    )
}

fun UserResponseDto.User.toEntity(timestamp: Long = System.currentTimeMillis()): UserEntity {
    return UserEntity(
        id = id,
        email = email,
        firstName = firstName,
        lastName = lastName,
        avatar = avatar,
        lastUpdated = timestamp
    )
}

fun UserEntity.toDomain(): UserDomain {
    return UserDomain(
        id = id,
        email = email,
        firstName = firstName,
        lastName = lastName,
        avatar = avatar
    )
}