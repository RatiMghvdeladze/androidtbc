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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository @Inject constructor(
    private val context: Context
) {
    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val REMEMBER_ME = booleanPreferencesKey("remember_me")
        private val USER_ID = stringPreferencesKey("user_id")
    }

    suspend fun saveLoginSession(userId: String, rememberMe: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[REMEMBER_ME] = rememberMe
            preferences[USER_ID] = userId
        }
    }

    suspend fun clearLoginSession() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
            preferences[REMEMBER_ME] = false
            preferences.remove(USER_ID)
        }
    }

    val isLoggedInWithRememberMe: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            val isLoggedIn = preferences[IS_LOGGED_IN] ?: false
            val rememberMe = preferences[REMEMBER_ME] ?: false
            isLoggedIn && rememberMe
        }

    suspend fun checkLoginStateImmediately(): Boolean {
        return context.dataStore.data.first().let { preferences ->
            val isLoggedIn = preferences[IS_LOGGED_IN] ?: false
            val rememberMe = preferences[REMEMBER_ME] ?: false
            isLoggedIn && rememberMe
        }
    }
}