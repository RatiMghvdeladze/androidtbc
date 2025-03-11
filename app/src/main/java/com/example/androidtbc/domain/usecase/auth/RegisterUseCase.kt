package com.example.androidtbc.domain.usecase.auth

import com.example.androidtbc.domain.repository.AuthRepository
import com.example.androidtbc.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, repeatPassword: String): Flow<Resource<String>> {
        return authRepository.register(email, password)
    }
}