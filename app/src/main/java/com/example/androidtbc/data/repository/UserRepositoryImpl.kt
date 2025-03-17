package com.example.androidtbc.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.androidtbc.data.local.AppDatabase
import com.example.androidtbc.data.mapper.toDomain
import com.example.androidtbc.data.remote.api.ApiService
import com.example.androidtbc.domain.models.UserDomain
import com.example.androidtbc.domain.repository.UserRepository
import com.example.mysecondapp.data.common.ApiHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase,
    private val apiHelper: ApiHelper
) : UserRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getUsers(): Flow<PagingData<UserDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = 6,
                enablePlaceholders = false,
                prefetchDistance = 3,
                initialLoadSize = 12
            ),
            remoteMediator = UserRemoteMediator(apiService, appDatabase, apiHelper),
            pagingSourceFactory = { appDatabase.userDao().getAllUsers() }
        ).flow.map { pagingData ->
            pagingData.map { userEntity ->
                userEntity.toDomain()
            }
        }
    }
}