package com.example.androidtbc.domain.usecase

import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.manager.AccountManager
import com.example.androidtbc.domain.model.Account
import com.example.androidtbc.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    private val repository: AccountRepository,
    private val accountManager: AccountManager
) {
    operator fun invoke(): Flow<Resource<List<Account>>> = flow {
        emit(Resource.Loading(true))

        val cachedAccounts = accountManager.accounts.value

        if (cachedAccounts.isNotEmpty()) {
            // Return cached accounts immediately
            emit(Resource.Success(cachedAccounts))

            // Only fetch from repository during initial load
            if (isInitialLoad) {
                repository.getAccounts().collect { result ->
                    if (result is Resource.Success && accountManager.accounts.value.isEmpty()) {
                        accountManager.setAccounts(result.data)
                    } else if (result is Resource.Error) {
                        emit(result)
                    }
                }
                isInitialLoad = false
            }
        } else {
            // No cached accounts, fetch from repository
            repository.getAccounts().collect { result ->
                emit(result)
                if (result is Resource.Success) {
                    accountManager.setAccounts(result.data)
                }
            }
            isInitialLoad = false
        }
    }

    companion object {
        private var isInitialLoad = true
    }
}