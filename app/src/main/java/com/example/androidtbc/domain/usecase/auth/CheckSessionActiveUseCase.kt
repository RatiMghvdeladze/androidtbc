package com.example.androidtbc.domain.usecase.auth

import com.example.androidtbc.domain.repository.UserSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface CheckSessionActiveUseCase {
    operator fun invoke(): Flow<Boolean>
}

class CheckSessionActiveUseCaseImpl @Inject constructor(
    private val userSessionRepository: UserSessionRepository
) : CheckSessionActiveUseCase {
    override operator fun invoke(): Flow<Boolean> {
        return userSessionRepository.isSessionActive()
    }
}