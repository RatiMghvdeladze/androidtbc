package com.example.androidtbc.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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

    suspend fun clearUserData() {
        context.dataStore.edit { it.remove(EMAIL) }
    }

    companion object {
        private val EMAIL = stringPreferencesKey("email")
    }
}
