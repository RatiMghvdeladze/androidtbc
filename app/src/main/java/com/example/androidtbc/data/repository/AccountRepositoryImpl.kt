package com.example.androidtbc.data.repository

import com.example.androidtbc.data.remote.api.ApiService
import com.example.androidtbc.data.remote.model.toDomain
import com.example.androidtbc.data.utils.ApiHelper
import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.model.Account
import com.example.androidtbc.domain.model.ExchangeRate
import com.example.androidtbc.domain.model.TransferResult
import com.example.androidtbc.domain.model.ValidationResult
import com.example.androidtbc.domain.repository.AccountRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val apiHelper: ApiHelper
) : AccountRepository {

    // Define dispatcher for background operations
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    override fun getAccounts(): Flow<Resource<List<Account>>> =
        apiHelper.handleHttpRequest { apiService.getAccounts() }
            .map { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // Simple mapping without changing dispatchers
                        Resource.Success(resource.data.map { it.toDomain() })
                    }
                    is Resource.Error -> Resource.Error(resource.errorMessage)
                    is Resource.Loading -> Resource.Loading(resource.isLoading)
                }
            }
    override fun validateAccount(accountNumber: String): Flow<Resource<ValidationResult>> =
        apiHelper.handleHttpRequest { apiService.validateAccount(accountNumber) }
            .map { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // Process on the current dispatcher
                        val validationResult = resource.data.toDomain()
                        Resource.Success(validationResult)
                    }
                    is Resource.Error -> Resource.Error(resource.errorMessage)
                    is Resource.Loading -> Resource.Loading(resource.isLoading)
                }
            }
            .flowOn(defaultDispatcher)

    override fun getExchangeRate(fromCurrency: String, toCurrency: String): Flow<Resource<ExchangeRate>> =
        apiHelper.handleHttpRequest { apiService.getExchangeRate(fromCurrency, toCurrency) }
            .map { resource ->
                try {
                    when (resource) {
                        is Resource.Success -> {
                            // Process on the current dispatcher
                            val exchangeRate = resource.data.toDomain(fromCurrency, toCurrency)
                            Resource.Success(exchangeRate)
                        }
                        is Resource.Error -> Resource.Error(resource.errorMessage)
                        is Resource.Loading -> Resource.Loading(resource.isLoading)
                    }
                } catch (e: Exception) {
                    Resource.Error("Error processing exchange rate data: ${e.message}")
                }
            }
            .flowOn(defaultDispatcher)

    override fun transferMoney(fromAccount: String, toAccount: String, amount: Double): Flow<Resource<TransferResult>> =
        apiHelper.handleHttpRequest { apiService.transferMoney(fromAccount, toAccount, amount) }
            .map { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // Process on the current dispatcher
                        val transferResult = resource.data.toDomain()
                        Resource.Success(transferResult)
                    }
                    is Resource.Error -> Resource.Error(resource.errorMessage)
                    is Resource.Loading -> Resource.Loading(resource.isLoading)
                }
            }
            .flowOn(defaultDispatcher)
}