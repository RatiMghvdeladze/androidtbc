package com.example.androidtbc.presentation.transfer

sealed class TransferEffect {
    data class ShowSnackbar(val message: String) : TransferEffect()
    data class NavigateToSuccess(val transactionResult: String) : TransferEffect()
    data object ShowInsufficientFundsError : TransferEffect()
    data object ShowFromAccountBottomSheet : TransferEffect()
}