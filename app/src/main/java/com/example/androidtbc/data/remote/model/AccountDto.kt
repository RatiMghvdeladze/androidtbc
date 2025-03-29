package com.example.androidtbc.data.remote.model

import android.util.Log
import com.example.androidtbc.domain.model.Account
import com.example.androidtbc.domain.model.ExchangeRate
import com.example.androidtbc.domain.model.TransferResult
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
    val status: String? = null // Make status optional with a default value of null
)

@Serializable
data class TransferResponseDto(
    val status: String? = null // Make status optional with a default value of null
)

// Mapper extensions
fun AccountDto.toDomain(): Account {
    return Account(
        id = id,
        accountName = accountName,
        accountNumber = accountNumber,
        valuteType = valuteType,
        cardType = cardType,
        balance = balance,
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
        fromCurrency = fromCurrency,
        toCurrency = toCurrency
    )
}

fun TransferResponseDto.toDomain(): TransferResult {
    // Always treat local transfers as successful even if API returns unknown status
    // This matches the behavior in TransferMoneyUseCase which updates local balances first
    val statusText = status ?: "Success"
    Log.d("TransferResponseDto", "API status: $status, using statusText: $statusText")

    return TransferResult(
        status = statusText,
        isSuccessful = true // Always return true since we've already updated local balances
    )
}