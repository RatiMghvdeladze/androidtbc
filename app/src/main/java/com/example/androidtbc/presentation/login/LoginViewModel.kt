package com.example.androidtbc.presentation.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.R
import com.example.androidtbc.data.repository.UserPreferencesRepository
import com.example.androidtbc.utils.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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
    private val application: Application,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _loginState = MutableStateFlow<Resource<Unit>>(Resource.Idle)
    val loginState: StateFlow<Resource<Unit>> = _loginState.asStateFlow()

    fun signIn(email: String, password: String, rememberMe: Boolean) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginState.value = Resource.Error("Email and password must be filled")
            return
        }

        _loginState.value = Resource.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Save the login session with remember me preference
                    viewModelScope.launch {
                        userPreferencesRepository.saveLoginSession(auth.currentUser?.uid ?: "", rememberMe)
                        _loginState.value = Resource.Success(Unit)
                    }
                } else {
                    _loginState.value = Resource.Error(task.exception?.message ?: "Login failed")
                }
            }
    }

    fun signInWithGoogle(idToken: String, rememberMe: Boolean) {
        _loginState.value = Resource.Loading
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Save the login session with remember me preference
                    viewModelScope.launch {
                        userPreferencesRepository.saveLoginSession(auth.currentUser?.uid ?: "", rememberMe)
                        _loginState.value = Resource.Success(Unit)
                    }
                } else {
                    _loginState.value = Resource.Error(task.exception?.message ?: "Google Sign-In failed")
                }
            }
    }

    // Function to check if user is already logged in with "Remember Me"
    val isUserLoggedIn: Flow<Boolean> = userPreferencesRepository.isLoggedInWithRememberMe

    // In LoginViewModel.kt
    fun logout() {
        viewModelScope.launch {
            Log.d("LoginViewModel", "Logout initiated")

            // First clear the preferences to prevent auto-login
            userPreferencesRepository.clearLoginSession()

            // Verify the logout was successful
            val isStillLoggedIn = userPreferencesRepository.checkLoginStateImmediately()
            Log.d("LoginViewModel", "After logout, isStillLoggedIn: $isStillLoggedIn")

            if (isStillLoggedIn) {
                Log.e("LoginViewModel", "First logout attempt failed, trying again")
                // Try again with a different approach
                userPreferencesRepository.clearLoginSession()
            }

            // Then sign out from Firebase
            auth.signOut()
            Log.d("LoginViewModel", "Firebase signOut completed")
        }
    }
    fun resetState() {
        _loginState.value = Resource.Idle
    }

    fun getGoogleSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(application.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(application, gso)
    }
}