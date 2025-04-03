package com.example.androidtbc.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserSessionRepository {
    suspend fun logoutCompletely()
    fun getUserEmail(): Flow<String?>
    fun isSessionActive(): Flow<Boolean>
    suspend fun isRememberMeEnabled(): Boolean
}