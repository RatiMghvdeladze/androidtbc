package com.example.androidtbc.data.mapper

import com.example.androidtbc.data.remote.model.AccountDto
import com.example.androidtbc.data.remote.model.ExchangeRateResponseDto
import com.example.androidtbc.data.remote.model.ValidationResponseDto
import com.example.androidtbc.domain.model.Account
import com.example.androidtbc.domain.model.CardType
import com.example.androidtbc.domain.model.CurrencyType
import com.example.androidtbc.domain.model.ExchangeRate
import com.example.androidtbc.domain.model.ValidationResult

fun AccountDto.toDomain(): Account {
    val roundedBalance = (Math.round(balance * 100) / 100.0)

    return Account(
        id = id,
        accountName = accountName,
        accountNumber = accountNumber,
        valuteType = CurrencyType.fromString(valuteType),
        cardType = CardType.fromString(cardType),
        balance = roundedBalance,
        cardLogo = cardLogo
    )
}

fun ValidationResponseDto.toDomain(): ValidationResult {
    return ValidationResult(
        status = status,
        isValid = status.equals("Success", ignoreCase = true)
    )
}

fun ExchangeRateResponseDto.toDomain(fromCurrency: String, toCurrency: String): ExchangeRate {
    return ExchangeRate(
        rate = course,
        fromCurrency = CurrencyType.fromString(fromCurrency),
        toCurrency = CurrencyType.fromString(toCurrency)
    )
}