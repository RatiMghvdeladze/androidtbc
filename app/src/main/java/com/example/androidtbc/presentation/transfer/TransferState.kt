package com.example.androidtbc.presentation.transfer

import com.example.androidtbc.domain.model.ValidationResult
import com.example.androidtbc.presentation.model.AccountUI
import com.example.androidtbc.presentation.model.ExchangeRateUI

data class TransferState(
    val isLoading: Boolean = false,
    val accounts: List<AccountUI> = emptyList(),
    val fromAccount: AccountUI? = null,
    val toAccount: AccountUI? = null,
    val accountValidation: ValidationResult? = null,
    val exchangeRate: ExchangeRateUI? = null,
    val sellAmount: Double = 0.0,
    val receiveAmount: Double = 0.0,
    val showSameCurrencyInput: Boolean = true,
    val showDifferentCurrencyInputs: Boolean = false,
    val description: String = "",
    val error: String? = null,
    val transferSuccess: Boolean = false,
)