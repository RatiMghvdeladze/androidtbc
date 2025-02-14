package com.example.androidtbc.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.AuthService
import com.example.androidtbc.ItemDTO
import com.example.androidtbc.utils.Resource
import com.example.androidtbc.utils.handleHttpRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    private val _items = MutableStateFlow<Resource<List<ItemDTO>>>(Resource.Idle)
    val items: StateFlow<Resource<List<ItemDTO>>> = _items.asStateFlow()

    init {
        getItems()
    }

    private fun getItems() {
        viewModelScope.launch {
            _items.value = Resource.Loading
            _items.value = handleHttpRequest {
                authService.getItems()
            }
        }
    }
}