package com.example.androidtbc.data.repository

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.androidtbc.data.local.AppDatabase
import com.example.androidtbc.data.local.entity.UserEntity
import com.example.androidtbc.data.local.entity.UserRemoteKeys
import com.example.androidtbc.data.remote.api.AuthService
import com.example.androidtbc.utils.Resource
import com.example.androidtbc.utils.handleHttpRequest
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator(
    private val context: Context,
    private val authService: AuthService,
    private val appDatabase: AppDatabase
) : RemoteMediator<Int, UserEntity>() {

    companion object {
        private const val REFRESH_INTERVAL_MINUTES = 1L
    }

    override suspend fun initialize(): InitializeAction {
        val lastUpdate = appDatabase.userDao().getLastUpdate() ?: 0L
        return if (shouldRefresh(lastUpdate)) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, UserEntity>): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastKey = appDatabase.withTransaction {
                        appDatabase.userRemoteKeysDao().getLastRemoteKey()
                    }
                    lastKey?.nextPage ?: return MediatorResult.Success(endOfPaginationReached = true)
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
                        appDatabase.userRemoteKeysDao().clearAllRemoteKeys()
                    }

                    val nextPage = if (users.isEmpty()) null else page + 1
                    val keys = users.map { UserRemoteKeys(userId = it.id, nextPage = nextPage) }

                    appDatabase.userRemoteKeysDao().insertAll(keys)
                    appDatabase.userDao().insertAll(users)
                }

                return MediatorResult.Success(endOfPaginationReached = users.isEmpty())
            }

            return MediatorResult.Error(Exception("Network request failed"))

        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private fun shouldRefresh(lastUpdate: Long): Boolean {
        return System.currentTimeMillis() - lastUpdate >= TimeUnit.MINUTES.toMillis(REFRESH_INTERVAL_MINUTES)
    }

}
