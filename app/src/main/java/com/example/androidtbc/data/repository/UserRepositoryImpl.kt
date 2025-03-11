package com.example.androidtbc.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.androidtbc.data.local.AppDatabase
import com.example.androidtbc.data.remote.api.AuthService
import com.example.androidtbc.domain.mapper.UserMapper
import com.example.androidtbc.domain.model.User
import com.example.androidtbc.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val appDatabase: AppDatabase
) : UserRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getUsers(): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = 6,
                enablePlaceholders = false,
            ),
            remoteMediator = UserRemoteMediator(authService, appDatabase),
            pagingSourceFactory = { appDatabase.userDao().getAllUsers() }
        ).flow.map { pagingData ->
            pagingData.map { userEntity ->
                UserMapper.mapEntityToDomain(userEntity)
            }
        }
    }
}