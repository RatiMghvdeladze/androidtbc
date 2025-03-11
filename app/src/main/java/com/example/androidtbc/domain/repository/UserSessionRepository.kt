package com.example.androidtbc.domain.repository

import com.example.androidtbc.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

interface UserSessionRepository {
    suspend fun clearToken()
    suspend fun logoutCompletely()
    fun getUserEmail(): Flow<String?>
    fun isSessionActive(): Flow<Boolean>
    fun getUserSession(): Flow<UserSession?>
}