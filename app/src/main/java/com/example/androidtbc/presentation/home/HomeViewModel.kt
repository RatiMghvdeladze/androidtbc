package com.example.androidtbc.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.androidtbc.domain.model.User
import com.example.androidtbc.domain.repository.UserRepository
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
    private val repository: UserRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(HomeViewState())
    val viewState: StateFlow<HomeViewState> = _viewState.asStateFlow()

    private val _eventChannel = Channel<HomeEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val _users = MutableStateFlow<PagingData<User>>(PagingData.empty())
    val users: StateFlow<PagingData<User>> = _users
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = PagingData.empty()
        )

    init {
        processIntent(HomeIntent.LoadUsers)
    }

    fun processIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadUsers -> loadUsers()
            is HomeIntent.RetryLoading -> loadUsers()
            is HomeIntent.NavigateToProfile -> {
            }
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            try {
                repository.getUsers()
                    .cachedIn(viewModelScope)
                    .collect { pagingData ->
                        _users.value = pagingData
                    }
            } catch (e: Exception) {
                viewModelScope.launch {
                    _eventChannel.send(HomeEvent.ShowSnackbar("Error loading users: ${e.message}"))
                }
            }
        }
    }

    fun updateViewState(isLoading: Boolean, errorMessage: String? = null, isEmpty: Boolean = false) {
        _viewState.value = _viewState.value.copy(
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