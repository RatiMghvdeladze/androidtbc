package com.example.androidtbc

import retrofit2.Response
import retrofit2.http.GET

interface AuthService {
    @GET("6dc7f56b-8a07-4686-9d15-9d5f780b4549")
    suspend fun getItems(): Response<List<ItemDTO>>
}