package com.example.androidtbc.data.repository

import com.example.androidtbc.data.mapper.toDomain
import com.example.androidtbc.data.remote.api.ApiService
import com.example.androidtbc.data.remote.models.AuthUserRequest
import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.common.mapResource
import com.example.androidtbc.domain.datastore.DataStoreManager
import com.example.androidtbc.domain.datastore.PreferenceKey
import com.example.androidtbc.domain.repository.LoginRepository
import com.example.androidtbc.domain.usecase.validation.ValidateEmailUseCase
import com.example.androidtbc.domain.usecase.validation.ValidatePasswordUseCase
import com.example.androidtbc.domain.usecase.validation.ValidationResult
import com.example.mysecondapp.data.common.ApiHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val apiHelper: ApiHelper,
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) : LoginRepository {
    override suspend fun login(email: String, password: String, rememberMe: Boolean): Flow<Resource<String>> = flow{
       when(val result = validateEmailUseCase(email)){
           is ValidationResult.Error -> {
               emit(Resource.Error(result.errorMessage))
               return@flow
           }
           ValidationResult.Success -> {}
       }

        when(val result = validatePasswordUseCase(password)){
            is ValidationResult.Error -> {
                emit(Resource.Error(result.errorMessage))
                return@flow
            }
            ValidationResult.Success -> {}
        }

        apiHelper.handleHttpRequest {
            apiService.loginUser(AuthUserRequest(email, password))
        }.mapResource {
            it.toDomain()
        }.collect{ resource ->
            when(resource){
                is Resource.Success -> {
                    if(resource.data.token.isEmpty()){
                        emit(Resource.Error("Invalid credentials"))
                    }else{
                        saveUserSession(email, resource.data.token, rememberMe)
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

    override suspend fun saveUserSession(email: String, token: String, rememberMe: Boolean) {
        dataStoreManager.savePreference(PreferenceKey.Token, token)
        dataStoreManager.savePreference(PreferenceKey.RememberMe, rememberMe)
        dataStoreManager.savePreference(PreferenceKey.Email, email)
    }

}