package com.example.androidtbc.data.repository

import com.example.androidtbc.domain.datastore.DataStoreManager
import com.example.androidtbc.domain.datastore.PreferenceKey
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
        dataStoreManager.clearPreference(PreferenceKey.Token)
    }

    override suspend fun logoutCompletely() {
        dataStoreManager.clearPreference(
            PreferenceKey.Email,
            PreferenceKey.Token,
            PreferenceKey.RememberMe
        )
    }

    override fun getUserEmail(): Flow<String?> =
        dataStoreManager.getPreference(PreferenceKey.Email).map { email ->
            if (email.isEmpty()) null else email
        }

    override fun isSessionActive(): Flow<Boolean> =
        dataStoreManager.getPreference(PreferenceKey.Token).map { token ->
            token.isNotEmpty()
        }

    override fun getUserSession(): Flow<UserSession?> = combine(
        dataStoreManager.getPreference(PreferenceKey.Token),
        dataStoreManager.getPreference(PreferenceKey.Email)
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