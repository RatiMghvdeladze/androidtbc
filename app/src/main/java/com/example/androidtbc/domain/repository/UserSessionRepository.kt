package com.example.androidtbc.domain.repository

import com.example.androidtbc.domain.models.UserSessionDomain
import kotlinx.coroutines.flow.Flow

interface UserSessionRepository {
    suspend fun clearToken()
    suspend fun logoutCompletely()
    fun getUserEmail(): Flow<String?>
    fun isSessionActive(): Flow<Boolean>
    fun getUserSession(): Flow<UserSessionDomain?>
    suspend fun isRememberMeEnabled(): Boolean
}