package com.example.androidtbc.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.androidtbc.domain.models.UserDomain
import com.example.androidtbc.domain.usecase.user.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _eventChannel = Channel<HomeEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val _users = MutableStateFlow<PagingData<UserDomain>>(PagingData.empty())
    val users: StateFlow<PagingData<UserDomain>> = _users
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = PagingData.empty()
        )

    init {
        onEvent(HomeEvent.LoadUsers)
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadUsers -> loadUsers()
            is HomeEvent.RetryLoading -> loadUsers()
            is HomeEvent.NavigateToProfile -> {
                viewModelScope.launch {
                    _eventChannel.send(event)
                }
            }

            is HomeEvent.UpdateViewState -> {
                updateViewState(
                    isLoading = event.isLoading,
                    errorMessage = event.errorMessage,
                    isEmpty = event.isEmpty
                )
            }

            else -> {}
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            updateViewState(isLoading = true)
            getUsersUseCase()
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _users.value = pagingData
                    _eventChannel.send(HomeEvent.UserDataLoaded(pagingData))
                    updateViewState(
                        isLoading = false
                    )
                }
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
                _eventChannel.send(HomeEvent.ShowSnackbar(it))
            }
        }
    }
}