package com.example.androidtbc.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.repository.UserPreferencesRepository
import com.example.androidtbc.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _loginState = MutableStateFlow<Resource<Unit>>(Resource.Idle)
    val loginState: StateFlow<Resource<Unit>> = _loginState.asStateFlow()

    fun signIn(email: String, password: String, rememberMe: Boolean) {
        if (email.isEmpty() || password.isEmpty()) {
            val errorMessage = when {
                email.isEmpty() && password.isEmpty() -> "Email and password must be filled"
                email.isEmpty() -> "Email must be filled"
                else -> "Password must be filled"
            }
            _loginState.value = Resource.Error(errorMessage)
            return
        }

        _loginState.value = Resource.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch {
                        userPreferencesRepository.saveLoginSession(auth.currentUser?.uid ?: "", rememberMe)
                        _loginState.value = Resource.Success(Unit)
                    }
                } else {
                    _loginState.value = Resource.Error(task.exception?.message ?: "Login failed")
                }
            }
    }

    val isUserLoggedIn: Flow<Boolean> = userPreferencesRepository.isLoggedInWithRememberMe

    fun resetState() {
        _loginState.value = Resource.Idle
    }
}