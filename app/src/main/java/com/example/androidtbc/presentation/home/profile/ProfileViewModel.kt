package com.example.androidtbc.presentation.home.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.remote.dto.User
import com.example.androidtbc.data.repository.UserPreferencesRepository
import com.example.androidtbc.data.repository.UserRepository
import com.example.androidtbc.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _userProfile = MutableStateFlow<Resource<User>>(Resource.Idle)
    val userProfile: StateFlow<Resource<User>> = _userProfile

    private var cachedUserProfile: User? = null

    init {
        preloadUserProfile()
    }

    private fun preloadUserProfile() {
        viewModelScope.launch {
            if (cachedUserProfile == null) {
                getUserProfile()
            }
        }
    }

    fun getUserProfile() {
        viewModelScope.launch {
            cachedUserProfile?.let {
                if (_userProfile.value !is Resource.Error) {
                    _userProfile.value = Resource.Success(it)
                }
            }

            _userProfile.value = Resource.Loading

            userRepository.getUserInfo().fold(
                onSuccess = { user ->
                    cachedUserProfile = user
                    _userProfile.value = Resource.Success(user)
                },
                onFailure = { error ->
                    _userProfile.value = Resource.Error(error.message ?: "Unknown error occurred")
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.clearLoginSession()

            val isStillLoggedIn = userPreferencesRepository.checkLoginStateImmediately()

            if (isStillLoggedIn) {
                userPreferencesRepository.clearLoginSession()
            }

            auth.signOut()
        }
    }
}