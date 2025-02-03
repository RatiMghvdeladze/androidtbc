package com.example.androidtbc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _users = MutableStateFlow<List<UserEntity>>(emptyList())
    val users: StateFlow<List<UserEntity>> = _users.asStateFlow()

    init {
        viewModelScope.launch {
            repository.usersFlow.collect {
                _users.value = it
            }
        }
    }

    fun refreshUsers() {
        viewModelScope.launch {
            repository.fetchUsersFromApi()
        }
    }
}
