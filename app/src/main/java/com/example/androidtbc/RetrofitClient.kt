package com.example.androidtbc

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitClient {
    private const val BASE_URL = "https://run.mocky.io/v3/"

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private fun retrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build()
    }


    val apiService: ApiService = retrofit().create(ApiService::class.java)
}