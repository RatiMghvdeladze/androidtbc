package com.example.androidtbc.data.mapper

import com.example.androidtbc.data.remote.dto.LoginResponseDTO
import com.example.androidtbc.data.remote.dto.RegisterResponseDTO
import com.example.androidtbc.domain.model.AuthDomain

fun RegisterResponseDTO.toDomain(): AuthDomain {
    return AuthDomain(
        token = this.token,
        id = this.id
    )
}

fun LoginResponseDTO.toDomain(): AuthDomain {
    return AuthDomain(
        token = this.token
    )
}