package com.example.androidtbc.domain.usecase

import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.manager.AccountManager
import com.example.androidtbc.domain.model.Account
import com.example.androidtbc.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for retrieving account data, with special handling to preserve
 * local balance changes after transfers
 */
class GetAccountsUseCase @Inject constructor(
    private val repository: AccountRepository,
    private val accountManager: AccountManager
) {
    operator fun invoke(): Flow<Resource<List<Account>>> = flow {
        emit(Resource.Loading(true))

        // Get current accounts from AccountManager
        val cachedAccounts = accountManager.accounts.value

        if (cachedAccounts.isNotEmpty()) {
            // First emit the cached accounts to update UI immediately
            emit(Resource.Success(cachedAccounts))

            // Only fetch from repository during initial app load
            if (isInitialLoad) {
                repository.getAccounts().collect { result ->
                    // Only update AccountManager with API data during initial load
                    if (result is Resource.Success && accountManager.accounts.value.isEmpty()) {
                        accountManager.setAccounts(result.data)
                    }

                    // If there was an error, pass it through
                    if (result is Resource.Error) {
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
        // Track if this is the first app load
        // We only fetch from API on first load to avoid overwriting balance changes
        private var isInitialLoad = true
    }
}