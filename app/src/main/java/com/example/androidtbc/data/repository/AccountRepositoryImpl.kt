package com.example.androidtbc.data.repository

import com.example.androidtbc.data.mapper.toDomain
import com.example.androidtbc.data.remote.api.ApiService
import com.example.androidtbc.data.utils.ApiHelper
import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.common.mapResource
import com.example.androidtbc.domain.model.Account
import com.example.androidtbc.domain.model.ExchangeRate
import com.example.androidtbc.domain.model.ValidationResult
import com.example.androidtbc.domain.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val apiHelper: ApiHelper
) : AccountRepository {

    override fun getAccounts(): Flow<Resource<List<Account>>> =
        apiHelper.handleHttpRequest { apiService.getAccounts() }
            .mapResource { accountDtos -> accountDtos.map { it.toDomain() } }
            .flowOn(Dispatchers.Default)

    override fun validateAccount(accountNumber: String): Flow<Resource<ValidationResult>> =
        apiHelper.handleHttpRequest { apiService.validateAccount(accountNumber) }
            .mapResource { it.toDomain() }
            .flowOn(Dispatchers.Default)

    override fun getExchangeRate(fromCurrency: String, toCurrency: String): Flow<Resource<ExchangeRate>> =
        apiHelper.handleHttpRequest { apiService.getExchangeRate(fromCurrency, toCurrency) }
            .mapResource { it.toDomain(fromCurrency, toCurrency) }
            .flowOn(Dispatchers.Default)
}