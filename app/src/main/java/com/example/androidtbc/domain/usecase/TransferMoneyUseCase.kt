package com.example.androidtbc.domain.usecase

import android.util.Log
import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.manager.AccountManager
import com.example.androidtbc.domain.model.TransferResult
import com.example.androidtbc.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for transferring money between accounts
 * Performs validation and updates account balances in AccountManager
 */
class TransferMoneyUseCase @Inject constructor(
    private val repository: AccountRepository,
    private val accountManager: AccountManager,
    private val getExchangeRateUseCase: GetExchangeRateUseCase // Add this dependency
) {
    operator fun invoke(fromAccount: String, toAccount: String, amount: Double): Flow<Resource<TransferResult>> = flow {
        emit(Resource.Loading(true))
        Log.d("TransferMoneyUseCase", "Starting transfer of $amount from $fromAccount to $toAccount")

        try {
            // Normalize account numbers
            val normalizedFromAccount = fromAccount.replace(" ", "")
            val normalizedToAccount = toAccount.replace(" ", "")

            // Get source account
            val sourceAccount = accountManager.getAccount(normalizedFromAccount)
            if (sourceAccount == null) {
                emit(Resource.Error("Source account not found"))
                return@flow
            }

            // Get target account
            val targetAccount = accountManager.getAccount(normalizedToAccount)
            if (targetAccount == null) {
                // For external accounts we might not have them in the manager
                // This is fine, we'll just update the source account
                Log.d("TransferMoneyUseCase", "Target account not found locally, proceeding with source account update only")
            }

            // Check for sufficient funds
            if (sourceAccount.balance < amount) {
                emit(Resource.Error("Insufficient funds in your account"))
                return@flow
            }

            // Calculate the correct amount to add to the target account
            var amountToAdd = amount

            // If currencies differ and we have the target account, convert the amount
            if (targetAccount != null && sourceAccount.valuteType != targetAccount.valuteType) {
                Log.d("TransferMoneyUseCase", "Converting amount between different currencies")
                Log.d("TransferMoneyUseCase", "From: ${sourceAccount.valuteType}, To: ${targetAccount.valuteType}")

                // Get exchange rate
                var exchangeRate = 1.0
                getExchangeRateUseCase(sourceAccount.valuteType, targetAccount.valuteType).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            exchangeRate = result.data.rate
                            Log.d("TransferMoneyUseCase", "Got exchange rate: $exchangeRate")
                        }
                        is Resource.Error -> {
                            Log.e("TransferMoneyUseCase", "Error getting exchange rate: ${result.errorMessage}")
                            // Continue with default rate of 1.0
                        }
                        is Resource.Loading -> {
                            // Ignore loading state
                        }
                    }
                }

                // Convert the amount using the exchange rate
                amountToAdd = amount * exchangeRate
                Log.d("TransferMoneyUseCase", "Converted amount: $amount ${sourceAccount.valuteType} = $amountToAdd ${targetAccount.valuteType}")
            }

            // Perform the transfer with proper currency conversion
            val transferSuccess = accountManager.transferMoneyWithConversion(
                fromAccount = fromAccount,
                toAccount = toAccount,
                deductAmount = amount,
                addAmount = amountToAdd
            )

            if (!transferSuccess) {
                emit(Resource.Error("Failed to transfer money"))
                return@flow
            }

            // Always return success if we reached this point
            emit(Resource.Success(TransferResult("Success", true)))

        } catch (e: Exception) {
            Log.e("TransferMoneyUseCase", "General error: ${e.message}")
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        } finally {
            emit(Resource.Loading(false))
        }
    }
}