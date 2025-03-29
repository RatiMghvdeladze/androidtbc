package com.example.androidtbc.domain.usecase

import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.manager.AccountManager
import com.example.androidtbc.domain.model.TransferResult
import com.example.androidtbc.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TransferMoneyUseCase @Inject constructor(
    private val repository: AccountRepository,
    private val accountManager: AccountManager,
    private val getExchangeRateUseCase: GetExchangeRateUseCase
) {
    operator fun invoke(fromAccount: String, toAccount: String, amount: Double): Flow<Resource<TransferResult>> = flow {
        emit(Resource.Loading(true))

        // Get source account
        val sourceAccount = accountManager.getAccount(fromAccount) ?: run {
            emit(Resource.Error("Source account not found"))
            return@flow
        }

        // Check for sufficient funds
        if (sourceAccount.balance < amount) {
            emit(Resource.Error("Insufficient funds in your account"))
            return@flow
        }

        // Get target account
        val targetAccount = accountManager.getAccount(toAccount)

        // Calculate amount to add to target account
        var amountToAdd = amount

        // Handle currency conversion if needed
        if (targetAccount != null && sourceAccount.valuteType != targetAccount.valuteType) {
            getExchangeRateUseCase(sourceAccount.valuteType, targetAccount.valuteType).collect { result ->
                if (result is Resource.Success) {
                    amountToAdd = amount * result.data.rate
                }
            }
        }

        // Perform the local transfer first
        val localTransferSuccess = accountManager.transferMoneyWithConversion(
            fromAccount = fromAccount,
            toAccount = toAccount,
            deductAmount = amount,
            addAmount = amountToAdd
        )

        if (!localTransferSuccess) {
            emit(Resource.Error("Failed to transfer money"))
            return@flow
        }

        // Now notify the API of the transfer
        var apiTransferSuccess = true
        repository.transferMoney(fromAccount, toAccount, amount).collect { result ->
            when (result) {
                is Resource.Success -> {
                    // API call succeeded
                    apiTransferSuccess = result.data.isSuccessful
                }
                is Resource.Error -> {
                    // API failed, but we already updated local state, so log but continue
                    apiTransferSuccess = false
                }
                is Resource.Loading -> {
                    // Ignore loading state
                }
            }
        }

        // We prioritize local state, so return success even if API failed
        emit(Resource.Success(TransferResult("Success", true)))
        emit(Resource.Loading(false))
    }
}