package com.example.androidtbc.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.domain.usecase.auth.GetUserSessionUseCase
import com.example.androidtbc.domain.usecase.auth.LoginUseCase
import com.example.androidtbc.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getUserSessionUseCase: GetUserSessionUseCase
) : ViewModel() {
    private val _loginState = MutableStateFlow<Resource<String>>(Resource.Idle)
    val loginState: StateFlow<Resource<String>> = _loginState.asStateFlow()

    fun login(email: String, password: String, rememberMe: Boolean) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginState.value = Resource.Error("Please fill in both email and password fields")
            return
        }

        viewModelScope.launch {
            loginUseCase(email, password, rememberMe)
                .collect { result ->
                    _loginState.value = result
                }
        }
    }

    fun getEmail() = getUserSessionUseCase.getUserEmail()
}