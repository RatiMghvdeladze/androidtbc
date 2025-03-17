package com.example.androidtbc.domain.usecase.auth

import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface LoginUseCase {
    suspend operator fun invoke(email: String, password: String, rememberMe: Boolean): Flow<Resource<String>>
}

class LoginUseCaseImpl @Inject constructor(
    private val loginRepository: LoginRepository
) : LoginUseCase{
    override suspend operator fun invoke(email: String, password: String, rememberMe: Boolean): Flow<Resource<String>> {
        return loginRepository.login(email, password, rememberMe)
    }
}