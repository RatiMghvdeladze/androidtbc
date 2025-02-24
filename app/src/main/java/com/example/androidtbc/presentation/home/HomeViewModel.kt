package com.example.androidtbc.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.androidtbc.data.remote.dto.Result
import com.example.androidtbc.data.remote.paging.PopularMoviesPagingSource
import com.example.androidtbc.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    val popularMovies: Flow<PagingData<Result>> = Pager(
        config = PagingConfig(
            pageSize = 6, // Reduced from 20
            enablePlaceholders = false,
            prefetchDistance = 1, // Reduced from 2
            initialLoadSize = 6 // Reduced from 40
        ),
        pagingSourceFactory = { PopularMoviesPagingSource(movieRepository) }
    ).flow.cachedIn(viewModelScope)
}