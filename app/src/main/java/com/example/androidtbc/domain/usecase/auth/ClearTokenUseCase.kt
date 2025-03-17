package com.example.androidtbc.domain.usecase.auth

import com.example.androidtbc.domain.repository.UserSessionRepository
import javax.inject.Inject

interface ClearTokenUseCase {
    suspend operator fun invoke()
}

class ClearTokenUseCaseImpl @Inject constructor(
    private val userSessionRepository: UserSessionRepository
) : ClearTokenUseCase {
    override suspend operator fun invoke() {
        userSessionRepository.clearToken()
    }
}