package com.example.androidtbc.viewModels

import androidx.lifecycle.viewModelScope
import com.example.androidtbc.LocalDataStore
import kotlinx.coroutines.launch

class ProfileViewModel(val dataStore: LocalDataStore): BaseViewModel() {

    fun clearUserData() {
        viewModelScope.launch {
            dataStore.clearUserData()
        }
    }

    fun getEmail() = dataStore.getEmail()
}