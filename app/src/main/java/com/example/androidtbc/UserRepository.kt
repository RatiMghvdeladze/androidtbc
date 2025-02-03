package com.example.androidtbc

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository(
    private val userDao: UserDao,
    private val apiService: ApiService,
    private val context: Context
) {
    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    init {
        observeNetworkStatus()
    }

    private fun observeNetworkStatus() {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isOnline.value = true
            }

            override fun onLost(network: Network) {
                _isOnline.value = false
            }
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    fun getUsers(): Flow<List<User>> = userDao.getAllUsers()

    suspend fun refreshUsers() {
        if (!isOnline.value) return

        try {
            val users = apiService.getUsers()
            userDao.insertUsers(users)
        } catch (e: Exception) {
            // Handle error
        }
    }
}