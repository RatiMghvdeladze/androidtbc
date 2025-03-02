package com.example.androidtbc.presentation.home.tablayoutfragments.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.androidtbc.data.remote.paging.PopularMoviesPagingSource
import com.example.androidtbc.data.repository.MovieRepository
import com.example.androidtbc.presentation.mapper.toMovie
import com.example.androidtbc.presentation.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class PopularViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    val popularMovies: Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { PopularMoviesPagingSource(repository) }
    ).flow
        .map { pagingData -> pagingData.map { it.toMovie() } }
        .cachedIn(viewModelScope)
}