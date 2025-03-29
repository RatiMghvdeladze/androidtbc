package com.example.androidtbc.domain.validators

object TransferValidator {
    fun hasSufficientFunds(balance: Double, amount: Double): Boolean {
        return balance >= amount
    }
}