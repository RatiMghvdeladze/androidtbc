package com.example.androidtbc.data.repository

import com.example.androidtbc.data.local.LocalDataStore
import com.example.androidtbc.data.remote.api.AuthService
import com.example.androidtbc.domain.model.LoginRawData
import com.example.androidtbc.domain.model.RegisterRawData
import com.example.androidtbc.domain.model.UserSession
import com.example.androidtbc.domain.repository.AuthRepository
import com.example.androidtbc.utils.Resource
import com.example.androidtbc.utils.Validator
import com.example.androidtbc.utils.handleHttpRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val dataStore: LocalDataStore,
    private val validator: Validator
) : AuthRepository {

    override suspend fun login(email: String, password: String, rememberMe: Boolean): Flow<Resource<String>> = flow {
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


    override suspend fun register(email: String, password: String): Flow<Resource<String>> = flow {
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
                    dataStore.saveToken(response.data.token)
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

    override suspend fun saveUserSession(email: String, token: String, rememberMe: Boolean) {
        dataStore.saveToken(token)
        dataStore.saveRememberMeState(rememberMe)

        if (rememberMe) {
            dataStore.saveEmail(email)
        }
    }

    override suspend fun clearToken() {
        dataStore.clearUserToken()
    }

    override suspend fun logoutCompletely() {
        dataStore.clearAllUserData()
    }

    override fun getUserEmail(): Flow<String?> = dataStore.getEmail().map { email ->
        if (email.isEmpty()) null else email
    }

    override fun isSessionActive(): Flow<Boolean> = dataStore.getToken().map { token ->
        token.isNotEmpty()
    }

    override fun getUserSession(): Flow<UserSession?> = combine(
        dataStore.getToken(),
        dataStore.getEmail()
    ) { token, email ->
        if (token.isNotEmpty()) {
            UserSession(
                email = email,
                token = token,
                isActive = true
            )
        } else {
            null
        }
    }
}