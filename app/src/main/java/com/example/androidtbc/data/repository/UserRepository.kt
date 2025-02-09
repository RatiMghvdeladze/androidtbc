package com.example.androidtbc.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.androidtbc.data.local.AppDatabase
import com.example.androidtbc.data.local.entity.UserEntity
import com.example.androidtbc.data.remote.api.AuthService
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val authService: AuthService,
    private val appDatabase: AppDatabase
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getUsers(): Flow<PagingData<UserEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 6,
                enablePlaceholders = false
            ),
            remoteMediator = UserRemoteMediator(authService, appDatabase),
            pagingSourceFactory = {
                appDatabase.userDao().getAllUsers()
            }
        ).flow
    }
}
