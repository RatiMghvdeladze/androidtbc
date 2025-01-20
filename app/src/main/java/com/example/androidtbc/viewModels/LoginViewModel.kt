package com.example.androidtbc.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.rawDataClasses.LoginRawData
import com.example.androidtbc.responseDtoClasses.LoginResponseDTO
import com.example.androidtbc.RetrofitClient
import com.example.androidtbc.Validatior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private val _flowData: MutableStateFlow<Response<LoginResponseDTO>?> = MutableStateFlow(null)
    val flowData = _flowData.asStateFlow()

    private val validate = Validatior()

    fun validateEmail(email: String) = validate.validateEmail(email)
    fun validatePassword(password: String) = validate.validatePassword(password)

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val responseData =
                    RetrofitClient.authService.loginUser(LoginRawData(email, password))
                _flowData.emit(responseData)
            } catch (e: Exception) {
            }
        }

    }
}
