package com.example.androidtbc

import java.util.Calendar

object CardValidator {

    fun isValidCardholderName(name: String?): Boolean {
        return !name.isNullOrBlank()
    }

    fun isValidCardNumber(number: String): Boolean {
        return number.length == 16 && number.all { it.isDigit() }
    }

    fun isValidExpiryDate(expiryDate: String): Boolean {
        return expiryDate.matches(Regex("^(0[1-9]|1[0-2])/([0-9]{2})\$")) && !isExpiryDateExpired(
            expiryDate
        )
    }

    private fun isExpiryDateExpired(expiryDate: String): Boolean {
        val parts = expiryDate.split("/")
        val month = parts[0].toInt()
        val year = 2000 + parts[1].toInt()

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1

        return when {
            year < currentYear -> true
            year == currentYear && month < currentMonth -> true
            else -> false
        }
    }

    fun isValidCvv(cvv: String): Boolean {
        return cvv.length == 3 && cvv.all { it.isDigit() }
    }

}
