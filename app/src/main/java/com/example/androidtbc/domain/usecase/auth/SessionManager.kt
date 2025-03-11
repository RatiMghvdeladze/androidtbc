package com.example.androidtbc.domain.usecase.auth

import com.example.androidtbc.domain.model.UserSession
import com.example.androidtbc.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SessionManager @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun getUserSession(): Flow<UserSession?> {
        return authRepository.getUserSession()
    }

    fun getUserEmail(): Flow<String?> {
        return authRepository.getUserEmail()
    }

    fun isSessionActive(): Flow<Boolean> {
        return authRepository.isSessionActive()
    }

    suspend fun clearSession(completeLogout: Boolean = false) {
        if (completeLogout) {
            authRepository.logoutCompletely()
        } else {
            authRepository.clearToken()
        }
    }

    suspend fun saveSession(email: String, token: String, rememberMe: Boolean) {
        authRepository.saveUserSession(email, token, rememberMe)
    }
}