package com.example.androidtbc.domain.usecase.auth

import com.example.androidtbc.domain.models.UserSessionDomain
import com.example.androidtbc.domain.repository.UserSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface FetchUserSessionUseCase {
    operator fun invoke(): Flow<UserSessionDomain?>
}

class FetchUserSessionUseCaseImpl @Inject constructor(
    private val userSessionRepository: UserSessionRepository
) : FetchUserSessionUseCase {
    override operator fun invoke(): Flow<UserSessionDomain?> {
        return userSessionRepository.getUserSession()
    }
}