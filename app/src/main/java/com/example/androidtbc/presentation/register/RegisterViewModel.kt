package com.example.androidtbc.presentation.register

import androidx.lifecycle.ViewModel
import com.example.androidtbc.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor()  : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _registerState = MutableStateFlow<Resource<Unit>>(Resource.Idle)
    val registerState: StateFlow<Resource<Unit>> = _registerState

    fun signUp(email: String, password: String, repeatPassword: String) {
        if (email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            _registerState.value = Resource.Error("All fields must be filled")
            return
        }

        if (password != repeatPassword) {
            _registerState.value = Resource.Error("Passwords don't match")
            return
        }

        _registerState.value = Resource.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _registerState.value = Resource.Success(Unit)
                } else {
                    _registerState.value = Resource.Error(task.exception?.message ?: "Registration failed")
                }
            }
    }

    fun signUpWithGoogle() {
    }

    fun resetState() {
        _registerState.value = Resource.Idle
    }
}