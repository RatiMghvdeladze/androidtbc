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
     * Transfers money between accounts, updating account balances locally
     * Returns true if the transfer was successful
     */
    fun transferMoney(fromAccountNumber: String, toAccountNumber: String, amount: Double): Boolean {
        // Normalize account numbers
        val normalizedFromAccount = fromAccountNumber.replace(" ", "")
        val normalizedToAccount = toAccountNumber.replace(" ", "")

        // Find source account
        val fromAccount = getAccount(fromAccountNumber) ?: return false

        // Check for sufficient funds
        if (fromAccount.balance < amount) return false

        // Create updated source account with new balance
        val updatedFromAccount = fromAccount.copy(balance = fromAccount.balance - amount)

        // Find and update destination account if it exists in our list
        val toAccount = getAccount(toAccountNumber)
        val updatedToAccount = toAccount?.copy(balance = toAccount.balance + amount)

        // Create new list with updated account balances
        val updatedAccounts = _accounts.value.map { account ->
            when {
                account.accountNumber.replace(" ", "") == normalizedFromAccount -> updatedFromAccount
                toAccount != null && account.accountNumber.replace(" ", "") == normalizedToAccount ->
                    updatedToAccount ?: account
                else -> account
            }
        }

        // Update accounts list
        _accounts.value = updatedAccounts

        // Mark that a transfer has occurred
        hasPerformedTransfer = true

        // Return success if the source account balance was updated correctly
        return getAccount(fromAccountNumber)?.balance == updatedFromAccount.balance
    }
}