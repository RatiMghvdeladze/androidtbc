package com.example.androidtbc.domain.usecase

import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.model.ExchangeRate
import com.example.androidtbc.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetExchangeRateUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    operator fun invoke(fromCurrency: String, toCurrency: String): Flow<Resource<ExchangeRate>> = flow {
        emit(Resource.Loading(true))

        // If currencies are the same, return 1:1 rate
        if (fromCurrency == toCurrency) {
            emit(Resource.Success(ExchangeRate(1.0, fromCurrency, toCurrency)))
            return@flow
        }

        // Use hardcoded rates for specific currency pairs
        val fallbackRate = getFallbackRate(fromCurrency, toCurrency)
        if (fallbackRate > 0) {
            emit(Resource.Success(ExchangeRate(fallbackRate, fromCurrency, toCurrency)))
            return@flow
        }

        // Try to get from repository as a fallback
        repository.getExchangeRate(fromCurrency, toCurrency)
            .collect { result ->
                when (result) {
                    is Resource.Success -> emit(result)
                    is Resource.Error -> {
                        val rate = getFallbackRate(fromCurrency, toCurrency)
                        emit(Resource.Success(ExchangeRate(rate, fromCurrency, toCurrency)))
                    }
                    is Resource.Loading -> emit(result)
                }
            }
    }

    private fun getFallbackRate(fromCurrency: String, toCurrency: String): Double = when {
        // GEL <-> EUR
        fromCurrency == "GEL" && toCurrency == "EUR" -> 0.25
        fromCurrency == "EUR" && toCurrency == "GEL" -> 4.0

        // USD <-> EUR
        fromCurrency == "USD" && toCurrency == "EUR" -> 0.93
        fromCurrency == "EUR" && toCurrency == "USD" -> 1.08

        // USD <-> GEL
        fromCurrency == "USD" && toCurrency == "GEL" -> 2.65
        fromCurrency == "GEL" && toCurrency == "USD" -> 0.38

        // Same currency
        fromCurrency == toCurrency -> 1.0

        // Default for unknown pairs
        else -> 1.0
    }
}