package com.example.androidtbc.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.local.LocalDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val dataStore: LocalDataStore): ViewModel() {

    fun clearUserData() {
        viewModelScope.launch {
            dataStore.clearUserData()
        }
    }

    fun getEmail() = dataStore.getEmail()
}