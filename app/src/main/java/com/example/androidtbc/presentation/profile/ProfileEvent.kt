package com.example.androidtbc.presentation.profile

sealed class ProfileEvent {
    data object CheckSessionStatus : ProfileEvent()
    data object LoadUserEmail : ProfileEvent()
    data object LogoutUser : ProfileEvent()
    data object NavigateToLogin : ProfileEvent()
    data class ShowSnackbar(val message: String) : ProfileEvent()
    data class UpdateViewState(
        val isLoading: Boolean,
        val errorMessage: String? = null,
        val isEmpty: Boolean = false
    ) : ProfileEvent()
}