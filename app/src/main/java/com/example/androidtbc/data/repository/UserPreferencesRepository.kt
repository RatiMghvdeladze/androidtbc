package com.example.androidtbc.data.repository

import android.content.Context
import android.util.Log
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

private const val TAG = "UserPreferencesRepo"
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
        Log.d(TAG, "Saving login session: userId=$userId, rememberMe=$rememberMe")
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[REMEMBER_ME] = rememberMe
            preferences[USER_ID] = userId
        }
        // Verify the save worked
        val saved = context.dataStore.data.first()
        Log.d(TAG, "Login session saved: isLoggedIn=${saved[IS_LOGGED_IN]}, rememberMe=${saved[REMEMBER_ME]}")
    }

    // Clear login session completely
    suspend fun clearLoginSession() {
        Log.d(TAG, "Clearing login session")
        try {
            context.dataStore.edit { preferences ->
                // Explicitly set all login-related preferences
                preferences[IS_LOGGED_IN] = false
                preferences[REMEMBER_ME] = false
                preferences.remove(USER_ID)
            }

            // Verify the clear worked by immediately reading back the values
            val result = context.dataStore.data.first()
            val isLoggedIn = result[IS_LOGGED_IN] ?: false
            val rememberMe = result[REMEMBER_ME] ?: false
            Log.d(TAG, "After clearing: isLoggedIn=$isLoggedIn, rememberMe=$rememberMe")

            if (isLoggedIn) {
                Log.e(TAG, "Failed to clear IS_LOGGED_IN flag, trying again with direct approach")
                // Try a different approach if clearing didn't work
                context.dataStore.edit { preferences ->
                    preferences.clear() // Try clearing everything as a fallback
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing login session", e)
        }
    }

    // Check if user is logged in with remember me enabled
    val isLoggedInWithRememberMe: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            val isLoggedIn = preferences[IS_LOGGED_IN] ?: false
            val rememberMe = preferences[REMEMBER_ME] ?: false
            val result = isLoggedIn && rememberMe
            Log.d(TAG, "Checking isLoggedInWithRememberMe: isLoggedIn=$isLoggedIn, rememberMe=$rememberMe, result=$result")
            result
        }

    // Get current login status regardless of remember me
    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            val result = preferences[IS_LOGGED_IN] ?: false
            Log.d(TAG, "Checking isLoggedIn: $result")
            result
        }

    // Get user ID
    val userId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ID]
        }

    // Get remember me setting
    val rememberMe: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[REMEMBER_ME] ?: false
        }

    // Add a method to check login state immediately (not as a Flow)
    suspend fun checkLoginStateImmediately(): Boolean {
        return context.dataStore.data.first().let { preferences ->
            val isLoggedIn = preferences[IS_LOGGED_IN] ?: false
            val rememberMe = preferences[REMEMBER_ME] ?: false
            Log.d(TAG, "Immediate check: isLoggedIn=$isLoggedIn, rememberMe=$rememberMe")
            isLoggedIn && rememberMe
        }
    }
}