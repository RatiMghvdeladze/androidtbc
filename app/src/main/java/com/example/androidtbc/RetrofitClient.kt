package com.example.androidtbc

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitClient {
    private const val BASE_URL = "https://reqres.in/api/"

    private fun retrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType())).build()
    }


    val authService: AuthService = retrofit().create(AuthService::class.java)
}