package com.example.androidtbc.data.remote.api

import com.example.androidtbc.data.remote.models.AuthResponseDto
import com.example.androidtbc.data.remote.models.AuthUserRequest
import com.example.androidtbc.data.remote.models.UserResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST(LOGIN)
    suspend fun loginUser(@Body authUserRequest: AuthUserRequest) : Response<AuthResponseDto>

    @POST(REGISTER)
    suspend fun registerUser(@Body authUserRequest: AuthUserRequest) : Response<AuthResponseDto>

    @GET(USERS)
    suspend fun getUsers(@Query("page") page: Int): Response<UserResponseDto>

    companion object{
        private const val REGISTER = "register"
        private const val LOGIN = "login"
        private const val USERS = "users"
    }
}