package com.example.androidtbc.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.androidtbc.domain.datastore.DataStoreManager
import com.example.androidtbc.domain.datastore.DataStorePreferencesKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

@Singleton
class DataStoreManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DataStoreManager {

    override suspend fun saveEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[DataStorePreferencesKeys.EMAIL] = email
        }
    }

    override fun getEmail(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[DataStorePreferencesKeys.EMAIL] ?: ""
        }
    }

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[DataStorePreferencesKeys.TOKEN] = token
        }
    }

    override fun getToken(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[DataStorePreferencesKeys.TOKEN] ?: ""
        }
    }

    override suspend fun saveRememberMeState(isRemembered: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DataStorePreferencesKeys.REMEMBER_ME] = isRemembered
        }
    }

    override fun getRememberMeState(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[DataStorePreferencesKeys.REMEMBER_ME] ?: false
        }
    }

    override suspend fun clearUserToken() {
        context.dataStore.edit {
            it.remove(DataStorePreferencesKeys.TOKEN)
        }
    }

    override suspend fun clearAllUserData() {
        context.dataStore.edit {
            it.remove(DataStorePreferencesKeys.EMAIL)
            it.remove(DataStorePreferencesKeys.TOKEN)
            it.remove(DataStorePreferencesKeys.REMEMBER_ME)
        }
    }
}