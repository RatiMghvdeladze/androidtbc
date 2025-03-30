package com.example.androidtbc.domain.model

data class ExchangeRate(
    val rate: Double,
    val fromCurrency: CurrencyType,
    val toCurrency: CurrencyType
)