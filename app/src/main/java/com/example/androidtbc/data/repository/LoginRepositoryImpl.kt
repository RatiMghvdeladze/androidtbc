package com.example.androidtbc.data.repository

import com.example.androidtbc.data.remote.api.AuthService
import com.example.androidtbc.domain.datastore.DataStoreManager
import com.example.androidtbc.domain.model.LoginRawData
import com.example.androidtbc.domain.repository.LoginRepository
import com.example.androidtbc.domain.usecase.validation.ValidatePasswordUseCase
import com.example.androidtbc.domain.validation.ValidationResult
import com.example.androidtbc.utils.Resource
import com.example.androidtbc.utils.handleHttpRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val dataStoreManager: DataStoreManager,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) : LoginRepository {

    override suspend fun login(email: String, password: String, rememberMe: Boolean): Flow<Resource<String>> = flow {
        if (!isValidEmail(email)) {
            emit(Resource.Error("Please enter a valid email address"))
            return@flow
        }

        when (val result = validatePasswordUseCase(password)) {
            is ValidationResult.Error -> {
                emit(Resource.Error(result.message))
                return@flow
            }
            else -> {}
        }

        emit(Resource.Loading)

        val response = handleHttpRequest {
            authService.loginUser(LoginRawData(email, password))
        }

        when (response) {
            is Resource.Success -> {
                if (response.data.token.isEmpty()) {
                    emit(Resource.Error("Invalid credentials"))
                } else {
                    saveUserSession(email, response.data.token, rememberMe)
                    emit(Resource.Success(email))
                }
            }
            is Resource.Error -> {
                emit(Resource.Error(response.errorMessage.ifEmpty {
                    "Failed to login. Please check your credentials and try again."
                }))
            }
            else -> Unit
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun saveUserSession(email: String, token: String, rememberMe: Boolean) {
        dataStoreManager.saveToken(token)
        dataStoreManager.saveRememberMeState(rememberMe)

        if (rememberMe) {
            dataStoreManager.saveEmail(email)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}