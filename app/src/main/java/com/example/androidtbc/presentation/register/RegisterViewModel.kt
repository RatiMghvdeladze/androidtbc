package com.example.androidtbc.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.repository.AuthRepository
import com.example.androidtbc.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _registrationState = MutableStateFlow<Resource<String>>(Resource.Idle)
    val registrationState: StateFlow<Resource<String>> = _registrationState.asStateFlow()

    fun register(email: String, password: String, repeatPassword: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _registrationState.value = Resource.Error("Please fill in all required fields")
            return
        }

        if (password != repeatPassword) {
            _registrationState.value = Resource.Error("Passwords do not match")
            return
        }

        viewModelScope.launch {
            authRepository.register(email, password)
                .collect { result ->
                    _registrationState.value = result
                }
        }
    }
}