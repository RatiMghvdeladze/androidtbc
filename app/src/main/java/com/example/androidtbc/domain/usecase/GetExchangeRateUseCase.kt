package com.example.androidtbc.domain.usecase

import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.model.CurrencyType
import com.example.androidtbc.domain.model.ExchangeRate
import com.example.androidtbc.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetExchangeRateUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    operator fun invoke(fromCurrency: CurrencyType, toCurrency: CurrencyType): Flow<Resource<ExchangeRate>> = flow {
        emit(Resource.Loading(true))

        if (fromCurrency == toCurrency) {
            emit(Resource.Success(ExchangeRate(1.0, fromCurrency, toCurrency)))
            return@flow
        }

        if (fromCurrency == CurrencyType.USD && toCurrency == CurrencyType.GEL) {
            repository.getExchangeRate(fromCurrency.value, toCurrency.value)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> emit(result)
                        is Resource.Error -> {
                            emit(Resource.Success(ExchangeRate(2.9, fromCurrency, toCurrency)))
                        }
                        is Resource.Loading -> emit(result)
                    }
                }
            return@flow
        }

        if (fromCurrency == CurrencyType.GEL && toCurrency == CurrencyType.USD) {
            repository.getExchangeRate(CurrencyType.USD.value, CurrencyType.GEL.value)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val invertedRate = 1.0 / result.data.rate
                            emit(Resource.Success(ExchangeRate(invertedRate, fromCurrency, toCurrency)))
                        }
                        is Resource.Error -> {
                            emit(Resource.Success(ExchangeRate(1.0 / 2.9, fromCurrency, toCurrency)))
                        }
                        is Resource.Loading -> emit(result)
                    }
                }
            return@flow
        }

        val rate = when {
            fromCurrency == CurrencyType.USD && toCurrency == CurrencyType.EUR -> 0.92
            fromCurrency == CurrencyType.EUR && toCurrency == CurrencyType.USD -> 1.0 / 0.92

            fromCurrency == CurrencyType.EUR && toCurrency == CurrencyType.GEL -> 2.9 / 0.92 // approximately 3.15
            fromCurrency == CurrencyType.GEL && toCurrency == CurrencyType.EUR -> 0.92 / 2.9 // approximately 0.32

            else -> 1.0
        }

        emit(Resource.Success(ExchangeRate(rate, fromCurrency, toCurrency)))
        emit(Resource.Loading(false))
    }
}