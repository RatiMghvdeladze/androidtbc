package com.example.androidtbc.data.remote.api

import com.example.androidtbc.data.remote.model.CategoryDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CategoryApiService {
    @GET(CATEGORY)
    suspend fun getCategories(@Query("search") search: String = ""): Response<List<CategoryDto>>

    companion object{
        private const val CATEGORY = "499e0ffd-db69-4955-8d86-86ee60755b9c"
    }
}