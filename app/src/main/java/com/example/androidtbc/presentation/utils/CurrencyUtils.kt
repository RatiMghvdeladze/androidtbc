package com.example.androidtbc.presentation.utils

import com.example.androidtbc.presentation.model.CurrencyTypeUI

object CurrencyUtils {
    fun getCurrencySymbol(currencyTypeUI: CurrencyTypeUI): String = when(currencyTypeUI) {
        CurrencyTypeUI.USD -> "$"
        CurrencyTypeUI.GEL -> "₾"
        CurrencyTypeUI.EUR -> "€"
    }

    fun formatAmountWithCurrency(amount: Double, currencyTypeUI: CurrencyTypeUI): String {
        val formattedAmount = String.format("%.2f", amount)
        return "$formattedAmount ${getCurrencySymbol(currencyTypeUI)}"
    }
}

