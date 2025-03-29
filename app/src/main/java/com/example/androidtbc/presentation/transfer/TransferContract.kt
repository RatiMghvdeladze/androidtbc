package com.example.androidtbc.presentation.transfer

import com.example.androidtbc.domain.model.ValidationResult
import com.example.androidtbc.presentation.model.AccountUI
import com.example.androidtbc.presentation.model.ExchangeRateUI
import com.example.androidtbc.presentation.model.TransactionResultUI

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
    val transactionResult: TransactionResultUI? = null
)

sealed class TransferEvent {
    data object LoadAccounts : TransferEvent()
    data class SelectFromAccount(val account: AccountUI) : TransferEvent()
    data class SelectToAccount(val account: AccountUI) : TransferEvent()
    data class ValidateAccount(val accountNumber: String, val validationType: String) : TransferEvent()
    data class UpdateSellAmount(val amount: Double) : TransferEvent()
    data class UpdateReceiveAmount(val amount: Double) : TransferEvent()
    data class UpdateDescription(val description: String) : TransferEvent()
    data class Transfer(val fromAccount: String, val toAccount: String, val amount: Double) : TransferEvent()
    data object ClearError : TransferEvent()
}

sealed class TransferEffect {
    data class ShowToast(val message: String) : TransferEffect()
    data class NavigateToSuccess(val transactionResult: String) : TransferEffect()
    data object ShowInsufficientFundsError : TransferEffect()
    data object ShowFromAccountBottomSheet : TransferEffect()
}