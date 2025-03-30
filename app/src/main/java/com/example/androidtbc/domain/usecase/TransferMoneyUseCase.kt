package com.example.androidtbc.domain.usecase

import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.manager.AccountManager
import com.example.androidtbc.domain.model.TransferResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TransferMoneyUseCase @Inject constructor(
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

        // Perform the local transfer
        val localTransferSuccess = accountManager.transferMoneyWithConversion(
            fromAccount = fromAccount,
            toAccount = toAccount,
            deductAmount = amount,
            addAmount = amountToAdd
        )

        if (localTransferSuccess) {
            emit(Resource.Success(TransferResult("Success", true)))
        } else {
            emit(Resource.Error("Failed to transfer money"))
        }

        emit(Resource.Loading(false))
    }
}