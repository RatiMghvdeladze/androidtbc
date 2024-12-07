package com.example.androidtbc

import android.util.Patterns

class InputValidator {
    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isFullNameValid(fullName: String): Boolean{
        val parts = fullName.trim().split("\\s+".toRegex())
        if (parts.size < 2) return false

        return parts.all { it.matches(Regex("^[a-zA-Z]+$"))}
    }
}