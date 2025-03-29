package com.example.androidtbc.domain.repository

import com.example.androidtbc.domain.model.Account
import com.example.androidtbc.domain.model.ExchangeRate
import com.example.androidtbc.domain.model.TransferResult
import com.example.androidtbc.domain.model.ValidationResult
import com.example.androidtbc.domain.common.Resource
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccounts(): Flow<Resource<List<Account>>>

    fun validateAccount(accountNumber: String): Flow<Resource<ValidationResult>>

    fun getExchangeRate(fromCurrency: String, toCurrency: String): Flow<Resource<ExchangeRate>>

    fun transferMoney(fromAccount: String, toAccount: String, amount: Double): Flow<Resource<TransferResult>>
}