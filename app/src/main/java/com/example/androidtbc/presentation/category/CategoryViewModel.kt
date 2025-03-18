package com.example.androidtbc.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.usecase.GetCategoriesUseCase
import com.example.androidtbc.presentation.mapper.toPresentationList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryState())
    val state: StateFlow<CategoryState> = _state.asStateFlow()

    private var searchJob: kotlinx.coroutines.Job? = null

    init {
        onEvent(CategoryEvent.LoadCategories)
    }

    fun onEvent(event: CategoryEvent) {
        when (event) {
            is CategoryEvent.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500)
                    loadCategories(event.query)
                }
            }
            is CategoryEvent.LoadCategories -> {
                loadCategories(_state.value.searchQuery)
            }
        }
    }

    private fun loadCategories(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getCategoriesUseCase(query).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                categories = result.data.toPresentationList(),
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                         _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.errorMessage
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = result.isLoading) }
                    }
                }
            }
        }
    }

}