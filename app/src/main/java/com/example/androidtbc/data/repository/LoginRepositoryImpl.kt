package com.example.androidtbc.data.repository

import com.example.androidtbc.data.mapper.toDomain
import com.example.androidtbc.data.remote.api.AuthService
import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.common.mapResource
import com.example.androidtbc.domain.datastore.DataStoreManager
import com.example.androidtbc.domain.model.LoginRawData
import com.example.androidtbc.domain.repository.LoginRepository
import com.example.androidtbc.domain.usecase.validation.ValidatePasswordUseCase
import com.example.androidtbc.domain.validation.ValidationResult
import com.example.mysecondapp.data.common.ApiHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val apiHelper: ApiHelper,
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

        apiHelper.handleHttpRequest {
            authService.loginUser(LoginRawData(email, password))
        }.mapResource {
            it.toDomain()
        }.collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    if (resource.data.token.isEmpty()) {
                        emit(Resource.Error("Invalid credentials"))
                    } else {
                        saveUserSession(email, resource.data.token, rememberMe)
                        emit(Resource.Success(email))
                    }
                }
                is Resource.Error -> {
                    emit(Resource.Error(resource.errorMessage.ifEmpty {
                        "Failed to login. Please check your credentials and try again."
                    }))
                }
                is Resource.Loading -> {
                    emit(Resource.Loading(resource.isLoading))
                }
                else -> {}
            }
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