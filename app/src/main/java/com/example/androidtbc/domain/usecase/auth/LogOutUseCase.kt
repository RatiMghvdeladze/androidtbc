package com.example.androidtbc.domain.usecase.auth

import com.example.androidtbc.domain.repository.UserSessionRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val userSessionRepository: UserSessionRepository
) {
    suspend operator fun invoke(completeLogout: Boolean = false) {
        if (completeLogout) {
            userSessionRepository.logoutCompletely()
        } else {
            userSessionRepository.clearToken()
        }
    }
}