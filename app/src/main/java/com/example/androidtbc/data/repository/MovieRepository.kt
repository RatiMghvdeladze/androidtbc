package com.example.androidtbc.data.repository

import com.example.androidtbc.data.remote.api.ApiService
import com.example.androidtbc.data.remote.dto.CastDto
import com.example.androidtbc.data.remote.dto.NowPlayingMovieDto
import com.example.androidtbc.data.remote.dto.PopularMovieDto
import com.example.androidtbc.data.remote.dto.TopRatedMovieDto
import com.example.androidtbc.data.remote.dto.UpcomingMovieDto
import com.example.androidtbc.utils.Resource
import com.example.androidtbc.utils.handleHttpRequest
import javax.inject.Inject

interface MovieRepository {
    suspend fun getPopularMovies(
        language: String = "en-US",
        page: Int = 1
    ): Resource<PopularMovieDto>

    suspend fun getNowPlayingMovies(
        language: String = "en-US",
        page: Int = 1
    ): Resource<NowPlayingMovieDto>

    suspend fun getTopRatedMovies(
        language: String = "en-US",
        page: Int = 1
    ): Resource<TopRatedMovieDto>

    suspend fun getUpcomingMovies(
        language: String = "en-US",
        page: Int = 1
    ): Resource<UpcomingMovieDto>

    suspend fun searchMovies(
        query: String,
        language: String = "en-US",
        page: Int = 1
    ): Resource<PopularMovieDto>


    suspend fun getMovieCast(movieId: Int): Resource<CastDto>
}

class MovieRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : MovieRepository {
    override suspend fun getPopularMovies(language: String, page: Int): Resource<PopularMovieDto> {
        return handleHttpRequest {
            apiService.getPopularMovies(language = language, page = page)
        }
    }

    override suspend fun getNowPlayingMovies(
        language: String,
        page: Int
    ): Resource<NowPlayingMovieDto> {
        return handleHttpRequest {
            apiService.getNowPlayingMovies(language = language, page = page)
        }
    }

    override suspend fun getTopRatedMovies(
        language: String,
        page: Int
    ): Resource<TopRatedMovieDto> {
        return handleHttpRequest {
            apiService.getTopRatedMovies(language = language, page = page)
        }
    }

    override suspend fun getUpcomingMovies(
        language: String,
        page: Int
    ): Resource<UpcomingMovieDto> {
        return handleHttpRequest {
            apiService.getUpcomingMovies(language = language, page = page)
        }
    }

    override suspend fun searchMovies(
        query: String,
        language: String,
        page: Int
    ): Resource<PopularMovieDto> {
        return handleHttpRequest {
            apiService.searchMovies(query = query, language = language, page = page)
        }
    }

    override suspend fun getMovieCast(movieId: Int): Resource<CastDto> {
        return handleHttpRequest {
            apiService.getMovieCredits(movieId)
        }
    }


}