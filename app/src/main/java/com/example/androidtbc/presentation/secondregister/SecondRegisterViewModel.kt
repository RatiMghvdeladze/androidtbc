package com.example.androidtbc.presentation.secondregister

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.repository.UserRepository
import com.example.androidtbc.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecondRegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userInfoState = MutableStateFlow<Resource<Unit>>(Resource.Idle)
    val userInfoState: StateFlow<Resource<Unit>> = _userInfoState

    fun saveUserInfo(name: String, phoneNumber: String, city: String) {
        viewModelScope.launch {
            _userInfoState.value = Resource.Loading

            userRepository.saveUserInfo(name, phoneNumber, city).fold(
                onSuccess = {
                    _userInfoState.value = Resource.Success(Unit)
                },
                onFailure = { error ->
                    _userInfoState.value = Resource.Error(error.message ?: "Unknown error occurred")
                }
            )
        }
    }

    fun resetState() {
        _userInfoState.value = Resource.Idle
    }
}