package com.example.androidtbc.domain.repository

import com.example.androidtbc.utils.Resource
import kotlinx.coroutines.flow.Flow

interface RegisterRepository {
    suspend fun register(email: String, password: String): Flow<Resource<String>>
}