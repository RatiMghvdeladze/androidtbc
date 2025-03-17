package com.example.androidtbc.domain.repository

import androidx.paging.PagingData
import com.example.androidtbc.domain.models.UserDomain
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers(): Flow<PagingData<UserDomain>>
}