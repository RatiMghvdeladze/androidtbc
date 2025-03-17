package com.example.androidtbc.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.androidtbc.data.local.AppDatabase
import com.example.androidtbc.data.local.entity.UserEntity
import com.example.androidtbc.data.mapper.toEntity
import com.example.androidtbc.data.remote.api.ApiService
import com.example.androidtbc.domain.common.Resource
import com.example.mysecondapp.data.common.ApiHelper
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator @Inject constructor(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase,
    private val apiHelper: ApiHelper
) : RemoteMediator<Int, UserEntity>() {

    private var currentPage = 1

    companion object {
        private const val REFRESH_INTERVAL_MINUTES = 60L
    }

    override suspend fun initialize(): InitializeAction {
        val lastUpdate = appDatabase.userDao().getLastUpdate() ?: 0L
        return if (shouldRefresh(lastUpdate)) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    private fun shouldRefresh(lastUpdate: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastUpdate) >= TimeUnit.MINUTES.toMillis(REFRESH_INTERVAL_MINUTES)
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, UserEntity>): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    currentPage = 1
                    1
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)

                    currentPage + 1
                }
            }

            val response = apiHelper.handleHttpRequest {
                apiService.getUsers(page)
            }.first {
                it !is Resource.Loading || !it.isLoading
            }

            when (response) {
                is Resource.Success -> {
                    val currentTime = System.currentTimeMillis()
                    val users = response.data.data.map { user ->
                        user.toEntity(currentTime)
                    }

                    appDatabase.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            appDatabase.userDao().clearAllUsers()
                        }

                        appDatabase.userDao().insertAll(users)
                        appDatabase.userDao().updateLastRefreshTime(currentTime)
                    }

                    if (loadType == LoadType.APPEND) {
                        currentPage = page
                    }

                    val endOfPaginationReached = users.isEmpty() ||
                            response.data.page >= response.data.totalPages

                    return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }
                is Resource.Error -> {
                    return MediatorResult.Error(Exception(response.errorMessage))
                }
                else -> {
                    return MediatorResult.Error(Exception("Unknown error occurred"))
                }
            }
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}