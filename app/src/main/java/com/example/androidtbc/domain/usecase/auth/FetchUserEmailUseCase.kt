package com.example.androidtbc.domain.usecase.auth

import com.example.androidtbc.domain.repository.UserSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface FetchUserEmailUseCase {
    operator fun invoke(): Flow<String?>
}

class FetchUserEmailUseCaseImpl @Inject constructor(
    private val userSessionRepository: UserSessionRepository
) : FetchUserEmailUseCase {
    override operator fun invoke(): Flow<String?> {
        return userSessionRepository.getUserEmail()
    }
}