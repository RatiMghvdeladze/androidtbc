package com.example.androidtbc.data.repository

import com.example.androidtbc.data.remote.api.ApiService
import com.example.androidtbc.data.remote.dto.MovieDetailDto
import com.example.androidtbc.utils.Resource
import com.example.androidtbc.utils.handleHttpRequest
import javax.inject.Inject

interface MovieDetailRepository {
    suspend fun getMovieDetails(movieId: Int, language: String = "en-US"): Resource<MovieDetailDto>
}

class MovieDetailRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : MovieDetailRepository {
    override suspend fun getMovieDetails(movieId: Int, language: String): Resource<MovieDetailDto> {
        return handleHttpRequest {
            apiService.getMovieDetails(movieId, language)
        }
    }
}