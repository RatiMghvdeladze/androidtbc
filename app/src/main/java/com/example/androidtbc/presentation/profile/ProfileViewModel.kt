package com.example.androidtbc.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            authRepository.clearToken()
            _isLoggingOut.value = true
        }
    }

    fun logoutCompletely() {
        viewModelScope.launch {
            authRepository.logoutCompletely()
            _isLoggingOut.value = true
        }
    }

    fun getEmail() = authRepository.getUserEmail()

    fun isSessionActive() = authRepository.isSessionActive()
}
