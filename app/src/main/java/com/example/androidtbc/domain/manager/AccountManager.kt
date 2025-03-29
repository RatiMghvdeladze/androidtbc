package com.example.androidtbc.domain.manager

import android.util.Log
import com.example.androidtbc.domain.model.Account
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages account balances and keeps track of changes
 * Acts as the source of truth for account data after transfers
 */
@Singleton
class AccountManager @Inject constructor() {
    // StateFlow to hold and observe account data
    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    // Flag to track if any transfers have occurred - prevents API data from overwriting local changes
    private var hasPerformedTransfer = false

    /**
     * Updates account list, but only if no transfers have been made
     * This prevents API data from overwriting our locally modified balances
     */
    fun setAccounts(accounts: List<Account>) {
        if (!hasPerformedTransfer) {
            Log.d("AccountManager", "Setting accounts: ${accounts.size}")
            _accounts.value = accounts
        } else {
            Log.d("AccountManager", "Ignoring API account update to preserve transfer changes")
        }
    }

    /**
     * Finds an account by its number, normalizing to remove spaces for consistent matching
     */
    fun getAccount(accountNumber: String): Account? {
        val normalizedInput = accountNumber.replace(" ", "")
        return _accounts.value.find {
            it.accountNumber.replace(" ", "") == normalizedInput
        }
    }

    /**
     * Transfers money between accounts with proper currency conversion
     * @param fromAccount Source account number
     * @param toAccount Destination account number
     * @param deductAmount Amount to deduct from source account (in source currency)
     * @param addAmount Amount to add to destination account (in destination currency)
     * @return true if transfer is successful
     */
    fun transferMoneyWithConversion(
        fromAccount: String,
        toAccount: String,
        deductAmount: Double,
        addAmount: Double
    ): Boolean {
        Log.d("AccountManager", "Transferring with conversion: deduct $deductAmount from $fromAccount, add $addAmount to $toAccount")

        // Normalize account numbers
        val normalizedFromAccount = fromAccount.replace(" ", "")
        val normalizedToAccount = toAccount.replace(" ", "")

        // Find source account
        val fromAccountObj = _accounts.value.find {
            it.accountNumber.replace(" ", "") == normalizedFromAccount
        }

        if (fromAccountObj == null) {
            Log.e("AccountManager", "Source account not found: $fromAccount")
            return false
        }

        // Check if sufficient funds
        if (fromAccountObj.balance < deductAmount) {
            Log.e("AccountManager", "Insufficient funds in account $fromAccount: ${fromAccountObj.balance} < $deductAmount")
            return false
        }

        // Create updated account with the new balance
        val updatedFromAccount = fromAccountObj.copy(balance = fromAccountObj.balance - deductAmount)
        Log.d("AccountManager", "FROM ACCOUNT - BEFORE: ${fromAccountObj.balance}, AFTER: ${updatedFromAccount.balance}")

        // Find destination account if it exists in our list
        val toAccountObj = _accounts.value.find {
            it.accountNumber.replace(" ", "") == normalizedToAccount
        }

        var updatedToAccount: Account? = null
        if (toAccountObj != null) {
            // Add the converted amount
            updatedToAccount = toAccountObj.copy(balance = toAccountObj.balance + addAmount)
            Log.d("AccountManager", "TO ACCOUNT - BEFORE: ${toAccountObj.balance}, AFTER: ${updatedToAccount.balance}")
            Log.d("AccountManager", "Currency conversion: $deductAmount ${fromAccountObj.valuteType} = $addAmount ${toAccountObj.valuteType}")
        } else {
            Log.d("AccountManager", "Target account not found in local list: $toAccount")
        }

        // Create a new list with updated accounts
        val updatedAccounts = _accounts.value.map { account ->
            when {
                account.accountNumber.replace(" ", "") == normalizedFromAccount -> {
                    Log.d("AccountManager", "Updating source account from ${account.balance} to ${updatedFromAccount.balance}")
                    updatedFromAccount
                }
                toAccountObj != null && account.accountNumber.replace(" ", "") == normalizedToAccount -> {
                    Log.d("AccountManager", "Updating target account from ${account.balance} to ${updatedToAccount?.balance}")
                    updatedToAccount ?: account
                }
                else -> account
            }
        }

        // Set the updated accounts list
        _accounts.value = updatedAccounts

        // Mark that we've performed a transfer
        hasPerformedTransfer = true

        // Verify the update happened
        val verifySource = getAccount(fromAccount)
        Log.d("AccountManager", "Verification - Source account balance: ${verifySource?.balance}")

        return verifySource?.balance == updatedFromAccount.balance
    }
}