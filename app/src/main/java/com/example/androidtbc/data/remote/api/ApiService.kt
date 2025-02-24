package com.example.androidtbc.data.remote.api

import com.example.androidtbc.data.remote.dto.NowPlayingMovieDto
import com.example.androidtbc.data.remote.dto.PopularMovieDto
import com.example.androidtbc.data.remote.dto.TopRatedMovieDto
import com.example.androidtbc.data.remote.dto.UpcomingMovieDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ) : Response<PopularMovieDto>

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ) : Response<NowPlayingMovieDto>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ) : Response<TopRatedMovieDto>


    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ) : Response<UpcomingMovieDto>


}