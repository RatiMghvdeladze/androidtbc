package com.example.androidtbc.presentation.category

sealed class CategoryEvent {
    data class OnSearchQueryChanged(val query: String) : CategoryEvent()
    data object LoadCategories : CategoryEvent()
}