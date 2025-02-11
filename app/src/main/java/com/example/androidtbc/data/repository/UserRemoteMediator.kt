package com.example.androidtbc.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.androidtbc.data.local.AppDatabase
import com.example.androidtbc.data.local.entity.UserEntity
import com.example.androidtbc.data.remote.api.AuthService
import com.example.androidtbc.utils.Resource
import com.example.androidtbc.utils.handleHttpRequest

@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator(
    private val authService: AuthService,
    private val appDatabase: AppDatabase
) : RemoteMediator<Int, UserEntity>() {

    private var currentPage = 1

    override suspend fun load(loadType: LoadType, state: PagingState<Int, UserEntity>): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    currentPage = 1
                    1
                }
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    currentPage + 1
                }
            }

            val response = handleHttpRequest { authService.getUsers(page) }
            if (response is Resource.Success) {
                val users = response.data.data.map { user ->
                    UserEntity(
                        id = user.id,
                        email = user.email,
                        firstName = user.firstName,
                        lastName = user.lastName,
                        avatar = user.avatar,
                        lastUpdated = System.currentTimeMillis()
                    )
                }

                appDatabase.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        appDatabase.userDao().clearAllUsers()
                    }
                    appDatabase.userDao().insertAll(users)
                }

                //if the request was successful update currentPage
                if (loadType == LoadType.APPEND) {
                    currentPage = page
                }

                //if we received less items than requested, end pagination
                val endOfPaginationReached = users.size < state.config.pageSize

                return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            }

            return MediatorResult.Error(Exception("Network request failed"))

        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}