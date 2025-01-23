package com.example.androidtbc.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.LocalDataStore
import kotlinx.coroutines.launch

class HomeViewModel(val dataStore: LocalDataStore): ViewModel() {

    fun clearUserData() {
        viewModelScope.launch {
            dataStore.clearUserData()
        }
    }

    fun getEmail() = dataStore.getEmail()
}