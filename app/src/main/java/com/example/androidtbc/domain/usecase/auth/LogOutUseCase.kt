package com.example.androidtbc.domain.usecase.auth

import com.example.androidtbc.domain.repository.AuthRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(completeLogout: Boolean = false) {
        if (completeLogout) {
            authRepository.logoutCompletely()
        } else {
            authRepository.clearToken()
        }
    }
}