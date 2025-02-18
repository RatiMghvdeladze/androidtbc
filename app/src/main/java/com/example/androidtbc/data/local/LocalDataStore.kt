package com.example.androidtbc.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

class LocalDataStore @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun saveEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[EMAIL] = email
        }
    }

    fun getEmail(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[EMAIL] ?: ""
        }
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    fun getToken(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN] ?: ""
        }
    }

    suspend fun saveRememberMeState(isRemembered: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMEMBER_ME] = isRemembered
        }
    }

    fun getRememberMeState(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[REMEMBER_ME] ?: false
        }
    }

    suspend fun clearUserToken() {
        context.dataStore.edit {
            it.remove(TOKEN)
        }
    }

    suspend fun clearRememberedSession() {
        context.dataStore.edit {
            it.remove(EMAIL)
            it.remove(REMEMBER_ME)
        }
    }

    suspend fun clearAllUserData() {
        context.dataStore.edit {
            it.remove(EMAIL)
            it.remove(TOKEN)
            it.remove(REMEMBER_ME)
        }
    }

    companion object {
        private val EMAIL = stringPreferencesKey("email")
        private val TOKEN = stringPreferencesKey("token")
        private val REMEMBER_ME = booleanPreferencesKey("remember_me")
    }
}
