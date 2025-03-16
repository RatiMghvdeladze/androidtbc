package com.example.androidtbc.domain.datastore

import androidx.datastore.preferences.core.Preferences

sealed class PreferenceKey<T>(val key: Preferences.Key<T>, val defaultValue: T) {
    data object Email : PreferenceKey<String>(DataStorePreferencesKeys.EMAIL, "")
    data object Token : PreferenceKey<String>(DataStorePreferencesKeys.TOKEN, "")
    data object RememberMe : PreferenceKey<Boolean>(DataStorePreferencesKeys.REMEMBER_ME, false)

}