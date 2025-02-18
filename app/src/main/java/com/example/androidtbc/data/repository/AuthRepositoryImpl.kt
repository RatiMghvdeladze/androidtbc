package com.example.androidtbc.data.repository

import com.example.androidtbc.data.local.LocalDataStore
import com.example.androidtbc.data.remote.api.AuthService
import com.example.androidtbc.domain.model.LoginRawData
import com.example.androidtbc.domain.model.RegisterRawData
import com.example.androidtbc.utils.Resource
import com.example.androidtbc.utils.Validator
import com.example.androidtbc.utils.handleHttpRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface AuthRepository {
    suspend fun login(email: String, password: String, rememberMe: Boolean): Flow<Resource<String>>
    suspend fun register(email: String, password: String): Flow<Resource<String>>
    suspend fun saveUserSession(email: String)
    suspend fun clearUserSession()
    fun getUserSession(): Flow<String?>
}

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val dataStore: LocalDataStore,
    private val validator: Validator
) : AuthRepository {

    override suspend fun login(email: String, password: String, rememberMe: Boolean): Flow<Resource<String>> = flow {
        // Input validation
        when {
            !validator.validateEmail(email) -> {
                emit(Resource.Error("Please enter a valid email address"))
                return@flow
            }
            !validator.validatePassword(password) -> {
                emit(Resource.Error("Password must contain at least 6 characters"))
                return@flow
            }
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
                    if (rememberMe) {
                        saveUserSession(email)
                    }
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


    override suspend fun register(email: String, password: String): Flow<Resource<String>> = flow {
        // Input validation
        when {
            !validator.validateEmail(email) -> {
                emit(Resource.Error("Please enter a valid email address"))
                return@flow
            }
            !validator.validatePassword(password) -> {
                emit(Resource.Error("Password must contain at least 6 characters"))
                return@flow
            }
        }

        emit(Resource.Loading)

        val response = handleHttpRequest {
            authService.registerUser(RegisterRawData(email, password))
        }

        when (response) {
            is Resource.Success -> {
                if (response.data.token.isEmpty()) {
                    emit(Resource.Error("Registration failed. This email might already be registered."))
                } else {
                    emit(Resource.Success(email))
                }
            }
            is Resource.Error -> {
                emit(Resource.Error(response.errorMessage.ifEmpty {
                    "Registration failed. Please try again."
                }))
            }
            else -> Unit
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun saveUserSession(email: String) {
        dataStore.saveEmail(email)
    }

    override suspend fun clearUserSession() {
        dataStore.clearUserData()
    }

    override fun getUserSession(): Flow<String?> = dataStore.getEmail()
}