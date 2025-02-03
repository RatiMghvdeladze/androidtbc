package com.example.androidtbc

import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val apiService: ApiService,
    private val userDao: UserDao
) {
    val usersFlow: Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun fetchUsersFromApi() {
        try {
            val users = apiService.getUsers().map {
                UserEntity(it.id, it.name, it.imageUrl, it.activation_status)
            }
            userDao.insertUsers(users)
        } catch (e: Exception) {

        }
    }
}
