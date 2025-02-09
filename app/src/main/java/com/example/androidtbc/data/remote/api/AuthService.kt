package com.example.androidtbc.data.remote.api

import com.example.androidtbc.domain.model.LoginRawData
import com.example.androidtbc.domain.model.RegisterRawData
import com.example.androidtbc.data.remote.dto.LoginResponseDTO
import com.example.androidtbc.data.remote.dto.RegisterResponseDTO
import com.example.androidtbc.data.remote.dto.UserResponseDTO
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