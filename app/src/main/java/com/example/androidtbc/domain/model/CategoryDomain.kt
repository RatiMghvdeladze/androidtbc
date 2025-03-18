package com.example.androidtbc.domain.model

data class CategoryDomain(
    val id: String,
    val name: String,
    val nameDe: String,
    val children: List<CategoryDomain> = emptyList(),
    val depth: Int = 0
)