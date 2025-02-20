package com.example.androidtbc.presentation.login

import androidx.lifecycle.ViewModel
import com.example.androidtbc.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _loginState = MutableStateFlow<Resource<Unit>>(Resource.Idle)
    val loginState : StateFlow<Resource<Unit>> = _loginState.asStateFlow()

    fun signIn(email: String, password: String){
        if(email.isEmpty() || password.isEmpty()){
            _loginState.value = Resource.Error("email and password must be filled")
            return
        }


        _loginState.value = Resource.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    _loginState.value = Resource.Success(Unit)
                }else {
                    _loginState.value = Resource.Error(task.exception?.message ?: "Login failed")
                }
            }
    }

    fun signInWithGoogle(){

    }

    fun resetState(){
        _loginState.value = Resource.Idle
    }

}