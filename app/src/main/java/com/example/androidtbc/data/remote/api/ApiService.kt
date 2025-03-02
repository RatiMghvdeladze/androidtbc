package com.example.androidtbc.data.remote.api

import com.example.androidtbc.data.remote.dto.CastDto
import com.example.androidtbc.data.remote.dto.MovieDetailDto
import com.example.androidtbc.data.remote.dto.NowPlayingMovieDto
import com.example.androidtbc.data.remote.dto.PopularMovieDto
import com.example.androidtbc.data.remote.dto.TopRatedMovieDto
import com.example.androidtbc.data.remote.dto.UpcomingMovieDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET(POPULAR_MOVIES)
    suspend fun getPopularMovies(
        @Query("language") language: String = DEFAULT_LANGUAGE,
        @Query("page") page: Int = DEFAULT_PAGE
    ) : Response<PopularMovieDto>

    @GET(NOW_PLAYING_MOVIES)
    suspend fun getNowPlayingMovies(
        @Query("language") language: String = DEFAULT_LANGUAGE,
        @Query("page") page: Int = DEFAULT_PAGE
    ) : Response<NowPlayingMovieDto>

    @GET(TOP_RATED_MOVIES)
    suspend fun getTopRatedMovies(
        @Query("language") language: String = DEFAULT_LANGUAGE,
        @Query("page") page: Int = DEFAULT_PAGE
    ) : Response<TopRatedMovieDto>

    @GET(UPCOMING_MOVIES)
    suspend fun getUpcomingMovies(
        @Query("language") language: String = DEFAULT_LANGUAGE,
        @Query("page") page: Int = DEFAULT_PAGE
    ) : Response<UpcomingMovieDto>

    @GET(SEARCH_MOVIES)
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("language") language: String = DEFAULT_LANGUAGE,
        @Query("page") page: Int = DEFAULT_PAGE
    ) : Response<PopularMovieDto>

    @GET(MOVIE_DETAILS)
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = DEFAULT_LANGUAGE
    ): Response<MovieDetailDto>

    @GET(MOVIE_CREDITS)
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = DEFAULT_LANGUAGE
    ): Response<CastDto>


    companion object {
        private const val DEFAULT_LANGUAGE = "en-US"
        private const val DEFAULT_PAGE = 1

        private const val POPULAR_MOVIES = "movie/popular"
        private const val NOW_PLAYING_MOVIES = "movie/now_playing"
        private const val TOP_RATED_MOVIES = "movie/top_rated"
        private const val UPCOMING_MOVIES = "movie/upcoming"
        private const val SEARCH_MOVIES = "search/movie"
        private const val MOVIE_DETAILS = "movie/{movie_id}"
        private const val MOVIE_CREDITS = "movie/{movie_id}/credits"
    }
}