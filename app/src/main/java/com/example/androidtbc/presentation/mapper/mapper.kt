package com.example.androidtbc.presentation.mapper

import androidx.paging.PagingData
import androidx.paging.map
import com.example.androidtbc.data.remote.dto.CastDto
import com.example.androidtbc.data.remote.dto.CastMemberDto
import com.example.androidtbc.data.remote.dto.MovieDetailDto
import com.example.androidtbc.data.remote.dto.MovieResult
import com.example.androidtbc.presentation.model.Cast
import com.example.androidtbc.presentation.model.CastMember
import com.example.androidtbc.presentation.model.Genre
import com.example.androidtbc.presentation.model.Movie
import com.example.androidtbc.presentation.model.MovieDetail
import com.example.androidtbc.presentation.model.MovieUI
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


fun MovieDetailDto.toMovieUI(): MovieUI {
    return MovieUI(
        id = id,
        title = title,
        posterPath = posterPath,
        voteAverage = voteAverage,
        releaseYear = releaseDate.split("-").firstOrNull() ?: "",
        runtime = "${runtime ?: 0} minutes",
        genreName = genres?.firstOrNull()?.name ?: "N/A"
    )
}

fun List<MovieDetailDto>.toMovieUIList(): List<MovieUI> {
    return this.map { it.toMovieUI() }
}




fun CastMemberDto.toCastMember(): CastMember {
    return CastMember(
        id = id,
        name = name,
        character = character,
        profilePath = profilePath,
        gender = gender,
        popularity = popularity
    )
}

fun CastDto.toCast(): Cast {
    return Cast(
        cast = cast.map { it.toCastMember() }
    )
}

fun List<CastMemberDto>.toCastMemberList(): List<CastMember> {
    return map { it.toCastMember() }
}

fun MovieDetailDto.toMovieDetail(): MovieDetail {
    return MovieDetail(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        runtime = runtime,
        status = status,
        budget = budget,
        revenue = revenue,
        originalLanguage = originalLanguage,
        genres = genres?.map { Genre(it.id, it.name) } ?: emptyList(),
        popularity = popularity,
        adult = adult
    )
}