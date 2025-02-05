package com.example.androidtbc

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PasscodeViewModel : ViewModel() {
    companion object {
        private const val CORRECT_PASSCODE = "0934"
    }

    private val _enteredPasscode = MutableStateFlow("")
    val enteredPasscode = _enteredPasscode.asStateFlow()

    private val _isSuccess = MutableStateFlow<Boolean?>(null)
    val isSuccess = _isSuccess.asStateFlow()


    fun insertDigit(digit: String) {
        if (_enteredPasscode.value.length < 4) {
            _enteredPasscode.value += digit
        }
        if (_enteredPasscode.value.length == 4) {
            checkPasscode()
        }

    }

    private fun checkPasscode() {
        if (_enteredPasscode.value == CORRECT_PASSCODE) {
            resetAllWhenCorrect()
        } else {
            resetAllWhenIncorrect()
        }
    }

    private fun resetAllWhenCorrect() {
        _isSuccess.value = true
        _enteredPasscode.value = ""
    }


    private fun resetAllWhenIncorrect() {
        _enteredPasscode.value = ""
        _isSuccess.value = false
    }

    fun deleteLastDigit() {
        if (_enteredPasscode.value.isNotEmpty()) {
            _enteredPasscode.value = _enteredPasscode.value.dropLast(1)
        }
    }

    fun setNull() {
        _isSuccess.value = null
    }

}