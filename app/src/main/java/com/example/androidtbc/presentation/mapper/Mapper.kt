package com.example.androidtbc.presentation.mapper

import com.example.androidtbc.domain.model.CategoryDomain
import com.example.androidtbc.presentation.model.CategoryPresentation

fun CategoryDomain.toPresentation(): CategoryPresentation {
    return CategoryPresentation(
        id = this.id,
        name = this.name,
        depth = this.depth
    )
}

fun List<CategoryDomain>.toPresentationList(): List<CategoryPresentation> {
    return this.map { it.toPresentation() }
}
