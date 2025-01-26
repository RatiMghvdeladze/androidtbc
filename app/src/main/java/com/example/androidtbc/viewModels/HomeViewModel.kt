package com.example.androidtbc.viewModels

import androidx.lifecycle.viewModelScope
import com.example.androidtbc.RetrofitClient
import com.example.androidtbc.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        if (isLoading || isLastPage) return

        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.authService.getUsers(currentPage)
                if (response.isSuccessful) {
                    response.body()?.data?.let { userList ->
                        _users.update { currentList -> currentList + userList }
                        if (userList.isEmpty()) {
                            isLastPage = true
                        } else {
                            currentPage++
                        }
                    }
                }
            } catch (e: Exception) {
                handleException(e)
            } finally {
                isLoading = false
            }
        }
    }
}
