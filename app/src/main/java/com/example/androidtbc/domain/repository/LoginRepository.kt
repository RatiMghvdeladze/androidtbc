package com.example.androidtbc.domain.repository

import com.example.androidtbc.utils.Resource
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun login(email: String, password: String, rememberMe: Boolean): Flow<Resource<String>>
    suspend fun saveUserSession(email: String, token: String, rememberMe: Boolean)
}