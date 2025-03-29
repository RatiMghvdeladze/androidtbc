package com.example.androidtbc.presentation.model

import android.os.Parcelable
import com.example.androidtbc.domain.model.Account
import com.example.androidtbc.domain.model.ExchangeRate
import kotlinx.parcelize.Parcelize

/**
 * Presentation model for Account
 */
@Parcelize
data class AccountUI(
    val id: Int,
    val accountName: String,
    val accountNumber: String,
    val valuteType: String,
    val cardType: String,
    val balance: Double,
    val cardLogo: String?,
    val maskedNumber: String
) : Parcelable {
    companion object {
        fun fromDomain(account: Account): AccountUI {
            val maskedNumber = if (account.accountNumber.length > 4) {
                "**** ${account.accountNumber.takeLast(4)}"
            } else {
                account.accountNumber
            }

            return AccountUI(
                id = account.id,
                accountName = account.accountName,
                accountNumber = account.accountNumber,
                valuteType = account.valuteType,
                cardType = account.cardType,
                balance = account.balance,
                cardLogo = account.cardLogo,
                maskedNumber = maskedNumber
            )
        }
    }
}


@Parcelize
data class ExchangeRateUI(
    val rate: Double,
    val fromCurrency: String,
    val toCurrency: String,
    val displayText: String
) : Parcelable {
    companion object {
        fun fromDomain(exchangeRate: ExchangeRate): ExchangeRateUI {
            // Get currency symbols
            val fromSymbol = getCurrencySymbol(exchangeRate.fromCurrency)
            val toSymbol = getCurrencySymbol(exchangeRate.toCurrency)

            // Format display text with the currency symbols - cleaned up and simplified
            val displayText = "1$fromSymbol = ${String.format("%.2f", exchangeRate.rate)}$toSymbol"

            return ExchangeRateUI(
                rate = exchangeRate.rate,
                fromCurrency = exchangeRate.fromCurrency,
                toCurrency = exchangeRate.toCurrency,
                displayText = displayText
            )
        }

        private fun getCurrencySymbol(currencyCode: String): String {
            return when (currencyCode) {
                "GEL" -> "₾"
                "EUR" -> "€"
                "USD" -> "$"
                else -> currencyCode
            }
        }
    }
}

data class TransactionResultUI(
    val isSuccessful: Boolean,
    val message: String,
    val fromAccount: AccountUI?,
    val toAccount: AccountUI?,
    val amount: Double,
    val date: String
)