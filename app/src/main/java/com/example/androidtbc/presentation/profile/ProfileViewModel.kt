package com.example.androidtbc.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.domain.usecase.auth.CheckSessionActiveUseCase
import com.example.androidtbc.domain.usecase.auth.CompleteLogoutUseCase
import com.example.androidtbc.domain.usecase.auth.FetchUserEmailUseCase
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
    private val checkSessionActiveUseCase: CheckSessionActiveUseCase,
    private val getUserEmailUseCase: FetchUserEmailUseCase,
    private val logoutUserUseCase: CompleteLogoutUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _eventChannel = Channel<ProfileEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        onEvent(ProfileEvent.CheckSessionStatus)
        onEvent(ProfileEvent.LoadUserEmail)
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.CheckSessionStatus -> checkSessionStatus()
            is ProfileEvent.LoadUserEmail -> loadUserEmail()
            is ProfileEvent.LogoutUser -> logoutUser()
            is ProfileEvent.UpdateViewState -> {
                updateViewState(
                    isLoading = event.isLoading,
                    errorMessage = event.errorMessage,
                    isEmpty = event.isEmpty
                )
            }

            else -> {
            }
        }
    }

    private fun checkSessionStatus() {
        viewModelScope.launch {
            checkSessionActiveUseCase().collect { isActive ->
                _state.value = _state.value.copy(isSessionActive = isActive)
                if (!isActive) {
                    _eventChannel.send(ProfileEvent.NavigateToLogin)
                }
            }

        }
    }

    private fun loadUserEmail() {
        viewModelScope.launch {
            getUserEmailUseCase().collect { email ->
                _state.value = _state.value.copy(userEmail = email)
            }
        }
    }

    private fun logoutUser() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            logoutUserUseCase()
            _state.value = _state.value.copy(isLoading = false, isSessionActive = false)
            _eventChannel.send(ProfileEvent.NavigateToLogin)
        }
    }

    private fun updateViewState(
        isLoading: Boolean,
        errorMessage: String? = null,
        isEmpty: Boolean = false
    ) {
        _state.value = _state.value.copy(
            isLoading = isLoading,
            errorMessage = errorMessage,
            isEmpty = isEmpty
        )

        errorMessage?.let {
            viewModelScope.launch {
                _eventChannel.send(ProfileEvent.ShowSnackbar(it))
            }
        }
    }
}