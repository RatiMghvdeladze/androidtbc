package com.example.androidtbc.data.remote.api

import com.example.androidtbc.data.remote.dto.ItemDTO
import retrofit2.Response
import retrofit2.http.GET

interface AuthService {
    @GET("6dffd14a-836f-4566-b024-bd41ace3a874")
    suspend fun getItems(): Response<List<ItemDTO>>
}