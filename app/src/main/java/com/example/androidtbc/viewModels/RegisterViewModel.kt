package com.example.androidtbc.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.RetrofitClient
import com.example.androidtbc.Validator
import com.example.androidtbc.rawDataClasses.RegisterRawData
import com.example.androidtbc.responseDtoClasses.RegisterResponseDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class RegisterViewModel: ViewModel() {
    private val _flowData: MutableStateFlow<Response<RegisterResponseDTO>?> = MutableStateFlow(null)
    val flowData = _flowData.asStateFlow()

    private val validator = Validator()


    fun validateEmail(email: String) = validator.validateEmail(email)
    fun validatePassword(password: String) = validator.validatePassword(password)

    fun register(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val responseData = RetrofitClient.authService.registerUser(RegisterRawData(email, password))
                _flowData.emit(responseData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}