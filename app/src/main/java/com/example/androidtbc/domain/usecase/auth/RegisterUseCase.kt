package com.example.androidtbc.domain.usecase.auth

import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.repository.RegisterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val registerRepository: RegisterRepository
) {
    suspend operator fun invoke(email: String, password: String, repeatPassword: String): Flow<Resource<String>> {
        return registerRepository.register(email, password)
    }
}