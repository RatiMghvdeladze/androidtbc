package com.example.androidtbc.presentation.model

import android.os.Parcelable
import com.example.androidtbc.domain.model.Account
import com.example.androidtbc.domain.model.ExchangeRate
import com.example.androidtbc.presentation.utils.CurrencyUtils
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountUI(
    val id: Int,
    val accountName: String,
    val accountNumber: String,
    val valuteType: CurrencyTypeUI,
    val cardType: CardTypeUI,
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

            // Round the balance to 2 decimal places
            val roundedBalance = (Math.round(account.balance * 100) / 100.0)

            return AccountUI(
                id = account.id,
                accountName = account.accountName,
                accountNumber = account.accountNumber,
                valuteType = CurrencyTypeUI.fromDomain(account.valuteType),
                cardType = CardTypeUI.fromDomain(account.cardType),
                balance = roundedBalance,
                cardLogo = account.cardLogo,
                maskedNumber = maskedNumber
            )
        }
    }
}

@Parcelize
data class ExchangeRateUI(
    val rate: Double,
    val fromCurrency: CurrencyTypeUI,
    val toCurrency: CurrencyTypeUI,
    val displayText: String
) : Parcelable {
    companion object {
        fun fromDomain(exchangeRate: ExchangeRate): ExchangeRateUI {
            // Convert domain currency types to presentation currency types
            val fromCurrencyUI = CurrencyTypeUI.fromDomain(exchangeRate.fromCurrency)
            val toCurrencyUI = CurrencyTypeUI.fromDomain(exchangeRate.toCurrency)

            // Get currency symbols using the utils with presentation types
            val fromSymbol = CurrencyUtils.getCurrencySymbol(fromCurrencyUI)
            val toSymbol = CurrencyUtils.getCurrencySymbol(toCurrencyUI)

            // Format display text with the currency symbols
            val displayText = "1$fromSymbol = ${String.format("%.2f", exchangeRate.rate)}$toSymbol"

            return ExchangeRateUI(
                rate = exchangeRate.rate,
                fromCurrency = fromCurrencyUI,
                toCurrency = toCurrencyUI,
                displayText = displayText
            )
        }

    }
}