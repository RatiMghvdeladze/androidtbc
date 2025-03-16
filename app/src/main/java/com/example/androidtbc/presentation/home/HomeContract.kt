package com.example.androidtbc.presentation.home

import androidx.paging.PagingData
import com.example.androidtbc.domain.model.User

sealed class HomeIntent {
    data object LoadUsers : HomeIntent()
    data object RetryLoading : HomeIntent()
    data object NavigateToProfile : HomeIntent()
}

data class HomeState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEmpty: Boolean = false
)

sealed class HomeEvent {
    data class NavigateToProfile(val email: String) : HomeEvent()
    data class ShowSnackbar(val message: String) : HomeEvent()
    data class UserDataLoaded(val pagingData: PagingData<User>) : HomeEvent()
}