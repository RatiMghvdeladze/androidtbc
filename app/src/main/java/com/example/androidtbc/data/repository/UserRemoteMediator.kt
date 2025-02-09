package com.example.androidtbc.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
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

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                if (lastItem == null) {
                    1
                } else {
                    (state.pages.size + 1)
                }
            }
        }

        return when (val result = handleHttpRequest { authService.getUsers(page) }) {
            is Resource.Success -> {
                val users = result.data.data.map { user ->
                    UserEntity(
                        id = user.id,
                        email = user.email,
                        firstName = user.firstName,
                        lastName = user.lastName,
                        avatar = user.avatar
                    )
                }

                if (loadType == LoadType.REFRESH) {
                    appDatabase.userDao().clearAllUsers()
                }

                appDatabase.userDao().insertAll(users)

                MediatorResult.Success(endOfPaginationReached = users.isEmpty())
            }

            is Resource.Error -> {
                MediatorResult.Error(Exception(result.errorMessage))
            }

            is Resource.Loading -> {
                MediatorResult.Success(endOfPaginationReached = false)
            }
        }
    }
}