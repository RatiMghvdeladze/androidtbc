package com.example.androidtbc.domain.datastore

import kotlinx.coroutines.flow.Flow

interface DataStoreManager {
    suspend fun <T> savePreference(key: PreferenceKey<T>, value: T)
    fun <T> getPreference(key: PreferenceKey<T>) : Flow<T>
    suspend fun clearPreference(vararg keys: PreferenceKey<*>)
    suspend fun clearAllPreferences()
}