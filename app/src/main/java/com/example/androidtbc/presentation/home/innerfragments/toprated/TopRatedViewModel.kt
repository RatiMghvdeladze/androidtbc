package com.example.androidtbc.presentation.home.innerfragments.toprated

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.androidtbc.data.remote.dto.Result
import com.example.androidtbc.data.remote.paging.TopRatedMoviesPagingSource
import com.example.androidtbc.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TopRatedViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    val topRatedMovies: Flow<PagingData<Result>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { TopRatedMoviesPagingSource(repository) }
    ).flow.cachedIn(viewModelScope)
}