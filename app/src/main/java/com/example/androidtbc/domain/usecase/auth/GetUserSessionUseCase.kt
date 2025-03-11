package com.example.androidtbc.domain.usecase.auth

import com.example.androidtbc.domain.model.UserSession
import com.example.androidtbc.domain.repository.UserSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserSessionUseCase @Inject constructor(
    private val userSessionRepository: UserSessionRepository
) {
    operator fun invoke(): Flow<UserSession?> {
        return userSessionRepository.getUserSession()
    }

    fun getUserEmail(): Flow<String?> {
        return userSessionRepository.getUserEmail()
    }

    fun isSessionActive(): Flow<Boolean> {
        return userSessionRepository.isSessionActive()
    }
}