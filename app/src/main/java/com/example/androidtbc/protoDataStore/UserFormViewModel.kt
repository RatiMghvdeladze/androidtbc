package com.example.androidtbc.protoDataStore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserFormViewModel(private val userProtoDataStore: UserProtoDataStore) : ViewModel() {

    private val _userData = MutableStateFlow<UserPreferences?>(null)
    val userData = _userData.asStateFlow()

    fun saveUserData(firstName: String, lastName: String, email: String) {
        viewModelScope.launch {
            userProtoDataStore.updateUserData(firstName, lastName, email)
        }
    }

    fun readUserData() {
        viewModelScope.launch {
            val preferences = userProtoDataStore.userPreferencesFlow.first()
            _userData.value = preferences
        }
    }
}