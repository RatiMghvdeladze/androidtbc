package com.example.androidtbc.domain.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStorePreferencesKeys {
    val EMAIL = stringPreferencesKey("email")
    val TOKEN = stringPreferencesKey("token")
    val REMEMBER_ME = booleanPreferencesKey("remember_me")
}