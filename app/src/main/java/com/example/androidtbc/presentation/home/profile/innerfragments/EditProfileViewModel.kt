package com.example.androidtbc.presentation.home.profile.innerfragments

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
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _userProfile = MutableStateFlow<Resource<User>>(Resource.Idle)
    val userProfile: StateFlow<Resource<User>> = _userProfile

    init {
        getUserProfile()
    }

    fun getUserProfile() {
        viewModelScope.launch {
            _userProfile.value = Resource.Loading

            userRepository.getUserInfo().fold(
                onSuccess = { user ->
                    _userProfile.value = Resource.Success(user)
                },
                onFailure = { error ->
                    _userProfile.value = Resource.Error(error.message ?: "Unknown error occurred")
                }
            )
        }
    }

    suspend fun saveUserInfo(name: String, phoneNumber: String, city: String): Result<Unit> {
        return userRepository.saveUserInfo(name, phoneNumber, city)
    }
}