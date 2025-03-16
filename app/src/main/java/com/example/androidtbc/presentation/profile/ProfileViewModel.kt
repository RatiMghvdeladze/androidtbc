package com.example.androidtbc.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.domain.repository.UserSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(ProfileState())
    val viewState: StateFlow<ProfileState> = _viewState.asStateFlow()

    private val _eventChannel = Channel<ProfileEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        processIntent(ProfileIntent.CheckSessionStatus)
    }

    fun processIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LogoutUser -> logoutUser()
            is ProfileIntent.CheckSessionStatus -> checkSessionStatus()
            is ProfileIntent.LoadUserEmail -> loadUserEmail()
        }
    }

    private fun logoutUser() {
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(isLoading = true)
            userSessionRepository.logoutCompletely()
            _viewState.value = _viewState.value.copy(isLoading = false, isSessionActive = false)
            _eventChannel.send(ProfileEvent.NavigateToLogin)
        }
    }

    private fun checkSessionStatus() {
        viewModelScope.launch {
            userSessionRepository.isSessionActive().collect { isActive ->
                _viewState.value = _viewState.value.copy(isSessionActive = isActive)
                if (!isActive) {
                    _eventChannel.send(ProfileEvent.NavigateToLogin)
                }
            }
        }
    }

    private fun loadUserEmail() {
        viewModelScope.launch {
            userSessionRepository.getUserEmail().collect { email ->
                _viewState.value = _viewState.value.copy(userEmail = email)
            }
        }
    }
}