package com.example.androidtbc.domain.validators

object AccountValidator {
    fun validateAccountNumber(accountNumber: String): Boolean {
        // Allow both exactly 22 and 23 characters to be more flexible
        val trimmed = accountNumber.trim()
        return trimmed.length >= 22 && trimmed.length <= 23
    }

    fun validatePersonalId(personalId: String): Boolean {
        // Personal ID should be 11 digits
        return personalId.trim().length == 11 && personalId.all { it.isDigit() }
    }

    fun validatePhoneNumber(phoneNumber: String): Boolean {
        // Phone number should be 9 digits
        return phoneNumber.trim().length == 9 && phoneNumber.all { it.isDigit() }
    }
}