package com.example.androidtbc

class InputValidator {
    fun isEverythingFill(arr: Array<String>): Boolean {
        return arr.all { it.isNotEmpty() }
    }

    fun isUsernameValid(username: String): Boolean {
        return username.length >= 10
    }

    fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9]+[A-Za-z0-9._%-]*@[a-zA-Z0-9.]+\\.[a-zA-Z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }

    fun isAgeValid(age: String?): Boolean {
        if (age.isNullOrEmpty()) return false
        val ageInt = age.toIntOrNull() ?: return false
        return ageInt > 0
    }
}
