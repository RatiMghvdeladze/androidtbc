package com.example.androidtbc.domain.repository

import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.model.CategoryDomain
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getCategories(search: String): Flow<Resource<List<CategoryDomain>>>
}