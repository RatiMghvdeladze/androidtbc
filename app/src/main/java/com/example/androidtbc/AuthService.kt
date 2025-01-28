package com.example.androidtbc

import com.example.androidtbc.rawDataClasses.LoginRawData
import com.example.androidtbc.rawDataClasses.RegisterRawData
import com.example.androidtbc.responseDtoClasses.LoginResponseDTO
import com.example.androidtbc.responseDtoClasses.RegisterResponseDTO
import com.example.androidtbc.responseDtoClasses.UserResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @POST(LOGIN)
    suspend fun loginUser(@Body loginRawData: LoginRawData) : Response<LoginResponseDTO>

    @POST(REGISTER)
    suspend fun registerUser(@Body registerRawData: RegisterRawData) : Response<RegisterResponseDTO>

    @GET(USERS)
    suspend fun getUsers(@Query("page") page: Int): Response<UserResponseDTO>

    companion object{
        private const val REGISTER = "register"
        private const val LOGIN = "login"
        private const val USERS = "users"
    }
}