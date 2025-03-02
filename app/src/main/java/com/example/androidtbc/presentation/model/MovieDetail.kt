package com.example.androidtbc.presentation.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val runtime: Int?,
    val status: String?,
    val budget: Long,
    val revenue: Long,
    val originalLanguage: String?,
    val genres: List<Genre>,
    val popularity: Double,
    val adult: Boolean
)

data class Genre(
    val id: Int,
    val name: String
)
