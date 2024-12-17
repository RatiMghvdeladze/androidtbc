package com.example.androidtbc

import android.util.Patterns

class InputValidator {
    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isAgeValid(age: Int): Boolean{
        return age > 0
    }

}