package com.example.androidtbc.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension property for Context to create a single DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val context: Context
) {
    // Keys for our preferences
    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val REMEMBER_ME = booleanPreferencesKey("remember_me")
        private val USER_ID = stringPreferencesKey("user_id")
    }

    // Save login session with remember me preference
    suspend fun saveLoginSession(userId: String, rememberMe: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[REMEMBER_ME] = rememberMe
            preferences[USER_ID] = userId
        }
    }

    // Clear login session completely
    suspend fun clearLoginSession() {
        context.dataStore.edit { preferences ->
            // Explicitly set all login-related preferences
            preferences[IS_LOGGED_IN] = false
            preferences[REMEMBER_ME] = false
            preferences.remove(USER_ID)
        }
    }

    // Check if user is logged in with remember me enabled
    val isLoggedInWithRememberMe: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            val isLoggedIn = preferences[IS_LOGGED_IN] ?: false
            val rememberMe = preferences[REMEMBER_ME] ?: false
            isLoggedIn && rememberMe
        }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }

    val userId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ID]
        }

    val rememberMe: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[REMEMBER_ME] ?: false
        }

    suspend fun checkLoginStateImmediately(): Boolean {
        return context.dataStore.data.first().let { preferences ->
            val isLoggedIn = preferences[IS_LOGGED_IN] ?: false
            val rememberMe = preferences[REMEMBER_ME] ?: false
            isLoggedIn && rememberMe
        }
    }
}