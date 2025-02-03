package com.example.androidtbc.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.example.androidtbc.retrofitApi.ApiService
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

    val usersFlow: Flow<List<UserEntity>> = userDao.getAllUsers()

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

    suspend fun refreshUsers() {
        if (!isOnline.value) {
            throw Exception("No internet connection")
        }

        val response = apiService.getUsers()
        val users = response.users.map { userResponse ->
            UserEntity(
                id = userResponse.id.toString(),
                name = "${userResponse.firstName} ${userResponse.lastName}".trim(),
                imageUrl = userResponse.avatar,
                activationStatus = userResponse.activationStatus.toInt()
            )
        }
        userDao.insertUsers(users)
    }
}