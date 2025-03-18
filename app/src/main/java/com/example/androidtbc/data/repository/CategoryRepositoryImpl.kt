package com.example.androidtbc.data.repository

import com.example.androidtbc.data.mapper.toDomainList
import com.example.androidtbc.data.remote.api.CategoryApiService
import com.example.androidtbc.data.utils.ApiHelper
import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.common.mapResource
import com.example.androidtbc.domain.model.CategoryDomain
import com.example.androidtbc.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val apiService: CategoryApiService,
    private val apiHelper: ApiHelper,
) : CategoryRepository {

    override suspend fun getCategories(search: String): Flow<Resource<List<CategoryDomain>>> {
        return apiHelper.handleHttpRequest {
            apiService.getCategories(search)
        }.mapResource { CategoryDtoList ->
            CategoryDtoList.toDomainList()
        }
    }
}
