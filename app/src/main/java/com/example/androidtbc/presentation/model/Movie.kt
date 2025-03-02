package com.example.androidtbc.presentation.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val backdropPath: String,
    val releaseDate: String,
    val voteAverage: Double,
    val popularity: Double,
    val adult: Boolean
)