package com.example.androidtbc.presentation.home

import androidx.paging.PagingData
import com.example.androidtbc.domain.models.UserDomain

data class HomeState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEmpty: Boolean = false
)

sealed class HomeEvent {
    data object LoadUsers : HomeEvent()
    data object RetryLoading : HomeEvent()
    data object NavigateToProfile : HomeEvent()
    data class ShowSnackbar(val message: String) : HomeEvent()
    data class UserDataLoaded(val pagingData: PagingData<UserDomain>) : HomeEvent()
    data class UpdateViewState(
        val isLoading: Boolean,
        val errorMessage: String? = null,
        val isEmpty: Boolean = false
    ) : HomeEvent()
}