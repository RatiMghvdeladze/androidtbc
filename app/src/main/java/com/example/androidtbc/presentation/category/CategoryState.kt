package com.example.androidtbc.presentation.category

import com.example.androidtbc.presentation.model.CategoryPresentation

data class CategoryState(
    val categories: List<CategoryPresentation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)