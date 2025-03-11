package com.example.androidtbc.data.repository

import com.example.androidtbc.domain.datastore.DataStoreManager
import com.example.androidtbc.domain.model.UserSession
import com.example.androidtbc.domain.repository.UserSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserSessionRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : UserSessionRepository {

    override suspend fun clearToken() {
        dataStoreManager.clearUserToken()
    }

    override suspend fun logoutCompletely() {
        dataStoreManager.clearAllUserData()
    }

    override fun getUserEmail(): Flow<String?> = dataStoreManager.getEmail().map { email ->
        if (email.isEmpty()) null else email
    }

    override fun isSessionActive(): Flow<Boolean> = dataStoreManager.getToken().map { token ->
        token.isNotEmpty()
    }

    override fun getUserSession(): Flow<UserSession?> = combine(
        dataStoreManager.getToken(),
        dataStoreManager.getEmail()
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