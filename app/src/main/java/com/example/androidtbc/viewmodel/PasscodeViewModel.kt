package com.example.androidtbc.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class PasscodeViewModel : ViewModel() {
    companion object {
        private const val CORRECT_PASSCODE = "0934"
    }

    fun getPasscodeLength() : Int = CORRECT_PASSCODE.length

    private val _enteredPasscode = MutableStateFlow("")
    val enteredPasscode = _enteredPasscode.asStateFlow()

    private val _uiEvent = Channel<String>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    fun insertDigit(digit: String) {
        if (_enteredPasscode.value.length < getPasscodeLength()) {
            _enteredPasscode.value += digit
        }
        if (_enteredPasscode.value.length == getPasscodeLength()) {
            checkPasscode()
        }
    }

    private fun checkPasscode() {
        if (_enteredPasscode.value == CORRECT_PASSCODE) {
            _uiEvent.trySend("Success")
        } else {
            _uiEvent.trySend("Incorrect passcode")
            _enteredPasscode.value = ""
        }
    }

    fun deleteLastDigit() {
        if (_enteredPasscode.value.isNotEmpty()) {
            _enteredPasscode.value = _enteredPasscode.value.dropLast(1)
        }
    }

    fun authenticateWithFingerprint() {
        _uiEvent.trySend("Fingerprint recognized!")
        _enteredPasscode.value = CORRECT_PASSCODE
    }
}
