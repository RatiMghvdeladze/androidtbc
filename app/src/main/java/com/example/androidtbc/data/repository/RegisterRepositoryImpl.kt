package com.example.androidtbc.data.repository

import com.example.androidtbc.data.mapper.toDomain
import com.example.androidtbc.data.remote.api.ApiService
import com.example.androidtbc.data.remote.models.AuthUserRequest
import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.common.mapResource
import com.example.androidtbc.domain.datastore.DataStoreManager
import com.example.androidtbc.domain.datastore.PreferenceKey
import com.example.androidtbc.domain.repository.RegisterRepository
import com.example.mysecondapp.data.common.ApiHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val apiHelper: ApiHelper,
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager,
) : RegisterRepository {
    override suspend fun register(email: String, password: String, repeatPassword: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading(isLoading = true))
        apiHelper.handleHttpRequest {
            apiService.registerUser(AuthUserRequest(email, password))
        }.mapResource {
            it.toDomain()
        }.collect{resource ->
            when(resource){
                is Resource.Success -> {
                    if (resource.data.token.isEmpty()) {
                        emit(Resource.Error("Registration failed"))
                    }else{
                        saveUserSession(email, resource.data.token)
                        emit(Resource.Success(email))
                    }
                }
                is Resource.Error -> {
                    emit(Resource.Error(resource.errorMessage))
                }
                is Resource.Loading -> {
                    emit(Resource.Loading(resource.isLoading))
                }

            }

        }


    }
    private suspend fun saveUserSession(email: String, token: String) {
        dataStoreManager.savePreference(PreferenceKey.Token, token)
        dataStoreManager.savePreference(PreferenceKey.Email, email)
    }
}