package com.example.androidtbc

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val EMAIL = "email"
        private const val REMEMBER_ME = "remember_me"
    }

    fun saveEmail(email: String) {
        sharedPreferences.edit().putString(EMAIL, email).apply()
    }

    fun saveRememberMe(rememberMe: Boolean) {
        sharedPreferences.edit().putBoolean(REMEMBER_ME, rememberMe).apply()
    }

    fun isRememberMeEnabled(): Boolean {
        return sharedPreferences.getBoolean(REMEMBER_ME, false)
    }

    fun getEmail(): String? {
        return sharedPreferences.getString(EMAIL, null)
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}
