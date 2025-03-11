package com.example.androidtbc.domain.usecase.auth

import com.example.androidtbc.domain.model.UserSession
import com.example.androidtbc.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<UserSession?> {
        return authRepository.getUserSession()
    }

    fun getUserEmail(): Flow<String?> {
        return authRepository.getUserEmail()
    }

    fun isSessionActive(): Flow<Boolean> {
        return authRepository.isSessionActive()
    }
}