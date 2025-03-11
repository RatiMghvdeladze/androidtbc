package com.example.androidtbc.domain.usecase.user

import androidx.paging.PagingData
import com.example.androidtbc.domain.model.User
import com.example.androidtbc.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<PagingData<User>> {
        return userRepository.getUsers()
    }
}