package com.example.androidtbc.data.remote.api

import com.example.androidtbc.data.remote.dto.PostDTO
import com.example.androidtbc.data.remote.dto.StoryDTO
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("00a18030-a8c7-47c4-b0c5-8bff92a29ebf")
    suspend fun getStories(): Response<List<StoryDTO>>

    @GET("1ba8b612-8391-41e5-8560-98e4a48decc7")
    suspend fun getPosts() : Response<List<PostDTO>>
}