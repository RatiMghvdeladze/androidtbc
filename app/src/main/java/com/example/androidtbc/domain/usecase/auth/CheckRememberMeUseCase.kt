package com.example.androidtbc.domain.usecase.auth

import com.example.androidtbc.domain.repository.UserSessionRepository
import javax.inject.Inject

interface CheckRememberMeUseCase {
    suspend operator fun invoke(): Boolean
}

class CheckRememberMeUseCaseImpl @Inject constructor(
    private val userSessionRepository: UserSessionRepository
) : CheckRememberMeUseCase {
    override suspend operator fun invoke(): Boolean {
        return userSessionRepository.isRememberMeEnabled()
    }
}