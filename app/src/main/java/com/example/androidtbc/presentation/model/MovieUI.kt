package com.example.androidtbc.presentation.model

data class MovieUI(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val voteAverage: Double,
    val releaseYear: String,
    val runtime: String,
    val genreName: String
)