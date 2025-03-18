package com.example.androidtbc.domain.usecase

import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.model.CategoryDomain
import com.example.androidtbc.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetCategoriesUseCase {
    suspend operator fun invoke(search: String): Flow<Resource<List<CategoryDomain>>>
}

class GetCategoriesUseCaseImpl @Inject constructor(
    private val categoryRepository: CategoryRepository
) : GetCategoriesUseCase {
    override suspend operator fun invoke(search: String): Flow<Resource<List<CategoryDomain>>> {
        return categoryRepository.getCategories(search).map { resource ->
            when (resource) {
                is Resource.Success -> {
                    val filteredCategories = if (search.isEmpty()) {
                        resource.data
                    } else {
                        resource.data.filter {
                            it.name.contains(search, ignoreCase = true) ||
                                    it.nameDe.contains(search, ignoreCase = true)
                        }
                    }
                    Resource.Success(filteredCategories)
                }
                else -> resource
            }
        }
    }
}