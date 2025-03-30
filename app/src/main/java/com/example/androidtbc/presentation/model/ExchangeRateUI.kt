package com.example.androidtbc.presentation.model

import android.os.Parcelable
import com.example.androidtbc.domain.model.ExchangeRate
import com.example.androidtbc.presentation.utils.CurrencyUtils
import kotlinx.parcelize.Parcelize


@Parcelize
data class ExchangeRateUI(
    val rate: Double,
    val fromCurrency: CurrencyTypeUI,
    val toCurrency: CurrencyTypeUI,
    val displayText: String
) : Parcelable {
    companion object {
        fun fromDomain(exchangeRate: ExchangeRate): ExchangeRateUI {
            val fromCurrencyUI = CurrencyTypeUI.fromDomain(exchangeRate.fromCurrency)
            val toCurrencyUI = CurrencyTypeUI.fromDomain(exchangeRate.toCurrency)

            val fromSymbol = CurrencyUtils.getCurrencySymbol(fromCurrencyUI)
            val toSymbol = CurrencyUtils.getCurrencySymbol(toCurrencyUI)

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