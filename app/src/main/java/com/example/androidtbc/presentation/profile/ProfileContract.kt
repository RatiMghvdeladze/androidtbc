package com.example.androidtbc.presentation.profile

sealed class ProfileIntent {
    data object LogoutUser : ProfileIntent()
    data object CheckSessionStatus : ProfileIntent()
    data object LoadUserEmail : ProfileIntent()
}

data class ProfileState(
    val isLoading: Boolean = false,
    val userEmail: String? = null,
    val isSessionActive: Boolean = true,
    val errorMessage: String? = null
)

sealed class ProfileEvent {
    data object NavigateToLogin : ProfileEvent()
    data class ShowSnackbar(val message: String) : ProfileEvent()
}