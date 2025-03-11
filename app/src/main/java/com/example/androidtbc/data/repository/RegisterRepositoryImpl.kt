package com.example.androidtbc.data.repository

import com.example.androidtbc.data.remote.api.AuthService
import com.example.androidtbc.domain.datastore.DataStoreManager
import com.example.androidtbc.domain.model.RegisterRawData
import com.example.androidtbc.domain.repository.RegisterRepository
import com.example.androidtbc.utils.Resource
import com.example.androidtbc.utils.Validator
import com.example.androidtbc.utils.handleHttpRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val dataStoreManager: DataStoreManager,
    private val validator: Validator
) : RegisterRepository {

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
                    dataStoreManager.saveToken(response.data.token)
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
}