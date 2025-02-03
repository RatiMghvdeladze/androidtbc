package com.example.androidtbc

import retrofit2.http.GET

interface ApiService {
    @GET("f3f41821-7434-471f-9baa-ae3dee984e6d")
    suspend fun getUsers(): List<UserResponse>
}


