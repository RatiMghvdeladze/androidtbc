package com.example.androidtbc.data.repository

import com.example.androidtbc.domain.datastore.DataStoreManager
import com.example.androidtbc.domain.datastore.PreferenceKey
import com.example.androidtbc.domain.repository.UserSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserSessionRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : UserSessionRepository {

    override suspend fun logoutCompletely() {
        dataStoreManager.clearPreference(
            PreferenceKey.Email,
            PreferenceKey.Token,
            PreferenceKey.RememberMe
        )
    }

    override fun getUserEmail(): Flow<String?> =
        dataStoreManager.getPreference(PreferenceKey.Email).map { email ->
            email.ifEmpty { null }
        }

    override fun isSessionActive(): Flow<Boolean> =
        dataStoreManager.getPreference(PreferenceKey.Token).map { token ->
            token.isNotEmpty()
        }
    override suspend fun isRememberMeEnabled(): Boolean {
        return dataStoreManager.getPreference(PreferenceKey.RememberMe)
            .first()
    }
}