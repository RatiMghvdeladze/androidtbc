package com.example.androidtbc.domain.repository

import com.example.androidtbc.domain.model.UserSession
import com.example.androidtbc.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String, rememberMe: Boolean): Flow<Resource<String>>
    suspend fun register(email: String, password: String): Flow<Resource<String>>
    suspend fun saveUserSession(email: String, token: String, rememberMe: Boolean)
    suspend fun clearToken()
    suspend fun logoutCompletely()
    fun getUserEmail(): Flow<String?>
    fun isSessionActive(): Flow<Boolean>
    fun getUserSession(): Flow<UserSession?>
}