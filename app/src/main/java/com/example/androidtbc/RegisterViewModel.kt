package com.example.androidtbc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private val _flowData : MutableStateFlow<Response<RegisterResponseDTO>?> = MutableStateFlow(null)
    val flowData = _flowData.asStateFlow()

    private val validate = Validatior()

    fun validateEmail(email: String) = validate.validateEmail(email)
    fun validatePassword(password: String) = validate.validatePassword(password)

    fun register(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val responseData = RetrofitClient.authService.registerUser(RegisterRawData(email, password))
                _flowData.emit(responseData)
            }catch(e: Exception){
            }
        }
    }


}