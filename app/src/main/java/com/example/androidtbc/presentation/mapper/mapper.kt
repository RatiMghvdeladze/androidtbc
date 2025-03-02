package com.example.androidtbc.presentation.mapper

import androidx.paging.PagingData
import androidx.paging.map
import com.example.androidtbc.data.remote.dto.MovieResult
import com.example.androidtbc.presentation.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun MovieResult.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        popularity = popularity,
        adult = adult
    )
}


fun List<MovieResult>.toMovieList(): List<Movie> {
    return map { it.toMovie() }
}


fun PagingData<MovieResult>.toMoviePagingData(): PagingData<Movie> {
    return this.map { it.toMovie() }
}

fun Flow<PagingData<MovieResult>>.toMoviePagingDataFlow(): Flow<PagingData<Movie>> {
    return this.map { it.toMoviePagingData() }
}