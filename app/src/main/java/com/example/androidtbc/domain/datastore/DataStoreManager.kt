package com.example.androidtbc.domain.datastore

import kotlinx.coroutines.flow.Flow

interface DataStoreManager {
    suspend fun saveEmail(email: String)
    fun getEmail(): Flow<String>

    suspend fun saveToken(token: String)
    fun getToken(): Flow<String>

    suspend fun saveRememberMeState(isRemembered: Boolean)
    fun getRememberMeState(): Flow<Boolean>

    suspend fun clearUserToken()
    suspend fun clearAllUserData()
}