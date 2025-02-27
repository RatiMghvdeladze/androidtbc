package com.example.androidtbc.presentation.home.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.remote.dto.User
import com.example.androidtbc.data.repository.UserRepository
import com.example.androidtbc.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<Resource<User>>(Resource.Idle)
    val userProfile: StateFlow<Resource<User>> = _userProfile

    // Cache for the user profile to avoid unnecessary network calls
    private var cachedUserProfile: User? = null

    init {
        // Pre-load user data when ViewModel is created
        preloadUserProfile()
    }

    private fun preloadUserProfile() {
        viewModelScope.launch {
            // Only load if we don't have a cached profile
            if (cachedUserProfile == null) {
                getUserProfile()
            }
        }
    }

    fun getUserProfile() {
        viewModelScope.launch {
            // If we have a cached profile and it's not an error state, emit it immediately
            cachedUserProfile?.let {
                if (_userProfile.value !is Resource.Error) {
                    _userProfile.value = Resource.Success(it)
                }
            }

            // Regardless of cache, start a fresh load
            _userProfile.value = Resource.Loading

            userRepository.getUserInfo().fold(
                onSuccess = { user ->
                    // Update cache
                    cachedUserProfile = user
                    _userProfile.value = Resource.Success(user)
                },
                onFailure = { error ->
                    _userProfile.value = Resource.Error(error.message ?: "Unknown error occurred")
                }
            )
        }
    }

    fun clearCache() {
        cachedUserProfile = null
    }

    // Call this method when you want to force a fresh load from the network
    fun refreshUserProfile() {
        cachedUserProfile = null
        getUserProfile()
    }

    // New function to save user info
    suspend fun saveUserInfo(name: String, phoneNumber: String, city: String): Result<Unit> {
        return userRepository.saveUserInfo(name, phoneNumber, city)
    }
}