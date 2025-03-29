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
    private val accountManager: AccountManager
) {
    operator fun invoke(fromAccount: String, toAccount: String, amount: Double): Flow<Resource<TransferResult>> = flow {
        emit(Resource.Loading(true))

        try {
            // Normalize account numbers
            val normalizedFromAccount = fromAccount.replace(" ", "")
            val normalizedToAccount = toAccount.replace(" ", "")

            // Verify source account exists
            val sourceAccount = accountManager.getAccount(normalizedFromAccount)
            if (sourceAccount == null) {
                emit(Resource.Error("Source account not found"))
                return@flow
            }

            // Check for sufficient funds
            if (sourceAccount.balance < amount) {
                emit(Resource.Error("Insufficient funds in your account"))
                return@flow
            }

            // Transfer money in AccountManager
            val transferSuccess = accountManager.transferMoney(fromAccount, toAccount, amount)

            if (!transferSuccess) {
                emit(Resource.Error("Failed to transfer money"))
                return@flow
            }

            // Verify transfer was successful by checking updated balance
            val verifiedAccount = accountManager.getAccount(normalizedFromAccount)
            val expectedBalance = sourceAccount.balance - amount

            if (verifiedAccount?.balance != expectedBalance) {
                Log.e("TransferMoneyUseCase", "Balance update verification failed!")
                emit(Resource.Error("Failed to update account balance"))
                return@flow
            }

            // Return success
            emit(Resource.Success(TransferResult("Success", true)))

        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        } finally {
            emit(Resource.Loading(false))
        }
    }
}