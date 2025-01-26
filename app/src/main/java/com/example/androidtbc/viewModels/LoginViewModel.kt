package com.example.androidtbc.viewModels

import androidx.lifecycle.viewModelScope
import com.example.androidtbc.LocalDataStore
import com.example.androidtbc.RetrofitClient
import com.example.androidtbc.Validator
import com.example.androidtbc.rawDataClasses.LoginRawData
import com.example.androidtbc.responseDtoClasses.LoginResponseDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(val dataStore: LocalDataStore) : BaseViewModel(){
    private val _flowData: MutableStateFlow<Response<LoginResponseDTO>?> = MutableStateFlow(null)
    val flowData = _flowData.asStateFlow()

    private val validator = Validator()


    fun saveEmail(email: String){
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.saveEmail(email)
        }
    }

    fun getEmail() = dataStore.getEmail()

    fun validateEmail(email: String) = validator.validateEmail(email)
    fun validatePassword(password: String) = validator.validatePassword(password)

    fun login(email: String, password: String, booleanIsChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val responseData = RetrofitClient.authService.loginUser(LoginRawData(email, password))
                _flowData.emit(responseData)
                if(responseData.isSuccessful && booleanIsChecked){
                    saveEmail(email)
                }
            } catch (e: Exception) {
                handleException(e)
            }

        }
    }

}
