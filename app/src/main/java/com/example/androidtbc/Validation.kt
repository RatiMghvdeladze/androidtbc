package com.example.androidtbc

import android.util.Patterns

class Validatior {
    fun validateEmail(email: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isValid = false
        }
        return isValid
    }

    fun validatePassword(password: String): Boolean {
        var isValid= true
        if (password.isEmpty()) {
            isValid = false
        } else if (password.length < 6) {
            isValid = false
        }
        return isValid
    }

}
