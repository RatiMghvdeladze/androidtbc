package com.example.androidtbc.presentation.home

data class HomeState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEmpty: Boolean = false
)