package com.example.androidtbc.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.local.LocalDataStore
import kotlinx.coroutines.launch

class ProfileViewModel(val dataStore: LocalDataStore): ViewModel() {

    fun clearUserData() {
        viewModelScope.launch {
            dataStore.clearUserData()
        }
    }

    fun getEmail() = dataStore.getEmail()
}