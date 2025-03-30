package com.example.androidtbc.data.remote.model

import com.example.androidtbc.domain.model.Account
import com.example.androidtbc.domain.model.CardType
import com.example.androidtbc.domain.model.CurrencyType
import com.example.androidtbc.domain.model.ExchangeRate
import com.example.androidtbc.domain.model.ValidationResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    val id: Int,
    @SerialName("account_name")
    val accountName: String,
    @SerialName("account_number")
    val accountNumber: String,
    @SerialName("valute_type")
    val valuteType: String,
    @SerialName("card_type")
    val cardType: String,
    val balance: Double,
    @SerialName("card_logo")
    val cardLogo: String?
)

@Serializable
data class ValidationResponseDto(
    val status: String
)

@Serializable
data class ExchangeRateResponseDto(
    val course: Double,
    val status: String? = null
)

fun AccountDto.toDomain(): Account {
    // Round the balance to 2 decimal places
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
    // We're making sure to use the course value directly from the API response
    return ExchangeRate(
        rate = course,
        fromCurrency = CurrencyType.fromString(fromCurrency),
        toCurrency = CurrencyType.fromString(toCurrency)
    )
}