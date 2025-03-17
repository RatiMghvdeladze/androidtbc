package com.example.androidtbc.domain.usecase.auth

import com.example.androidtbc.domain.repository.UserSessionRepository
import javax.inject.Inject

interface CompleteLogoutUseCase {
    suspend operator fun invoke()
}

class CompleteLogoutUseCaseImpl @Inject constructor(
    private val userSessionRepository: UserSessionRepository
) : CompleteLogoutUseCase {
    override suspend operator fun invoke() {
        userSessionRepository.logoutCompletely()
    }
}