package com.example.androidtbc.data.mapper

import com.example.androidtbc.data.remote.model.CategoryDto
import com.example.androidtbc.domain.model.CategoryDomain

fun List<CategoryDto>.toDomainList(): List<CategoryDomain> {
    val result = mutableListOf<CategoryDomain>()

    fun addCategoriesWithDepth(categories: List<CategoryDto>, depth: Int) {
        categories.forEach { category ->
            result.add(
                CategoryDomain(
                    id = category.id,
                    name = category.name,
                    nameDe = category.nameDe,
                    depth = depth
                )
            )

            if (category.children.isNotEmpty()) {
                addCategoriesWithDepth(category.children, depth + 1)
            }
        }
    }

    addCategoriesWithDepth(this, 0)
    return result
}