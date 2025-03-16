package com.example.androidtbc.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.androidtbc.domain.datastore.DataStoreManager
import com.example.androidtbc.domain.datastore.PreferenceKey
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

    override suspend fun <T> savePreference(key: PreferenceKey<T>, value: T) {
        context.dataStore.edit { preferences ->
            preferences[key.key] = value
        }
    }

    override fun <T> getPreference(key: PreferenceKey<T>): Flow<T> {
        return context.dataStore.data.map { preferences ->
            preferences[key.key] ?: key.defaultValue
        }
    }

    override suspend fun clearPreference(vararg keys: PreferenceKey<*>) {
        context.dataStore.edit { preferences ->
            keys.forEach { key ->
                preferences.remove(key.key)
            }
        }
    }

    override suspend fun clearAllPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

}