package com.example.androidtbc.presentation.transfer

import com.example.androidtbc.presentation.model.AccountUI
import com.example.androidtbc.presentation.model.ValidationTypeUI

sealed class TransferEvent {
    data object LoadAccounts : TransferEvent()
    data class SelectFromAccount(val account: AccountUI) : TransferEvent()
    data class SelectToAccount(val account: AccountUI) : TransferEvent()
    data class ValidateAccount(val accountNumber: String, val validationType: ValidationTypeUI) : TransferEvent()
    data class UpdateSellAmount(val amount: Double) : TransferEvent()
    data class UpdateReceiveAmount(val amount: Double) : TransferEvent()
    data class UpdateDescription(val description: String) : TransferEvent()
    data class Transfer(val fromAccount: String, val toAccount: String, val amount: Double) : TransferEvent()
    data object ClearError : TransferEvent()
    data object ClearInputs : TransferEvent()
}