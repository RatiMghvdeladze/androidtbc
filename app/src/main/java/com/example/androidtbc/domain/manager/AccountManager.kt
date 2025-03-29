package com.example.androidtbc.domain.manager

import com.example.androidtbc.domain.model.Account
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountManager @Inject constructor() {
    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()
    private var hasPerformedTransfer = false

    fun setAccounts(accounts: List<Account>) {
        if (!hasPerformedTransfer) {
            _accounts.value = accounts
        }
    }

    fun getAccount(accountNumber: String): Account? {
        val normalizedInput = accountNumber.replace(" ", "")
        return _accounts.value.find {
            it.accountNumber.replace(" ", "") == normalizedInput
        }
    }

    fun transferMoneyWithConversion(
        fromAccount: String,
        toAccount: String,
        deductAmount: Double,
        addAmount: Double
    ): Boolean {
        // Get normalized account numbers and accounts
        val normalizedFromAccount = fromAccount.replace(" ", "")
        val normalizedToAccount = toAccount.replace(" ", "")

        val fromAccountObj = getAccount(fromAccount) ?: return false

        // Check if sufficient funds
        if (fromAccountObj.balance < deductAmount) return false

        // Get destination account if it exists
        val toAccountObj = getAccount(toAccount)

        // Create updated accounts
        val updatedFromAccount = fromAccountObj.copy(balance = fromAccountObj.balance - deductAmount)
        val updatedToAccount = toAccountObj?.copy(balance = toAccountObj.balance + addAmount)

        // Create a new list with updated accounts
        val updatedAccounts = _accounts.value.map { account ->
            when {
                account.accountNumber.replace(" ", "") == normalizedFromAccount -> updatedFromAccount
                toAccountObj != null && account.accountNumber.replace(" ", "") == normalizedToAccount ->
                    updatedToAccount ?: account
                else -> account
            }
        }

        // Set the updated accounts list
        _accounts.value = updatedAccounts
        hasPerformedTransfer = true

        return true
    }
}