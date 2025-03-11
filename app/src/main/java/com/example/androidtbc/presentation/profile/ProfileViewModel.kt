package com.example.androidtbc.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.domain.repository.UserSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {

    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()


    fun logoutCompletely() {
        viewModelScope.launch {
            userSessionRepository.logoutCompletely()
            _isLoggingOut.value = true
        }
    }

    fun isSessionActive() = userSessionRepository.isSessionActive()
}
