package com.example.androidtbc.domain.validators

object AccountValidator {
    fun validateAccountNumber(accountNumber: String): Boolean {
        val trimmed = accountNumber.trim()
        return trimmed.length <= 23
    }

    fun validatePersonalId(personalId: String): Boolean {
        return personalId.trim().length == 11 && personalId.all { it.isDigit() }
    }

    fun validatePhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.trim().length == 9 && phoneNumber.all { it.isDigit() }
    }
}