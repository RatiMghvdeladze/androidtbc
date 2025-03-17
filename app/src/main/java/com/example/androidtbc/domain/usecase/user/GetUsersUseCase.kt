package com.example.androidtbc.domain.usecase.user

import androidx.paging.PagingData
import com.example.androidtbc.domain.models.UserDomain
import com.example.androidtbc.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetUsersUseCase {
    operator fun invoke(): Flow<PagingData<UserDomain>>
}

class GetUsersUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : GetUsersUseCase{
    override operator fun invoke(): Flow<PagingData<UserDomain>> {
        return userRepository.getUsers()
    }
}