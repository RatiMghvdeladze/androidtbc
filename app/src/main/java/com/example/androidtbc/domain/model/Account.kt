package com.example.androidtbc.domain.model

data class Account(
    val id: Int,
    val accountName: String,
    val accountNumber: String,
    val valuteType: String,
    val cardType: String,
    val balance: Double,
    val cardLogo: String?
)

data class ValidationResult(
    val status: String,
    val isValid: Boolean
)

data class ExchangeRate(
    val rate: Double,
    val fromCurrency: String,
    val toCurrency: String
)

data class TransferResult(
    val status: String,
    val isSuccessful: Boolean
)
