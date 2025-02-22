package com.example.androidtbc.data.remote.repository

import com.example.androidtbc.data.remote.api.ApiService
import com.example.androidtbc.data.remote.dto.PostDTO
import com.example.androidtbc.data.remote.dto.StoryDTO
import com.example.androidtbc.utils.Resource
import com.example.androidtbc.utils.handleHttpRequest
import javax.inject.Inject

interface HomeRepository {
    suspend fun getStories(): Resource<List<StoryDTO>>
    suspend fun getPosts(): Resource<List<PostDTO>>
}

class HomeRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : HomeRepository {

    override suspend fun getStories(): Resource<List<StoryDTO>> {
        return handleHttpRequest {
            apiService.getStories()
        }
    }

    override suspend fun getPosts(): Resource<List<PostDTO>> {
        return handleHttpRequest {
            apiService.getPosts()
        }
    }
}