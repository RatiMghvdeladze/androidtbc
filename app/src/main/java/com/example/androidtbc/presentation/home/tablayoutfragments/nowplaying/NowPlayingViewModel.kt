package com.example.androidtbc.presentation.home.tablayoutfragments.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.androidtbc.data.remote.dto.MovieResult
import com.example.androidtbc.data.remote.paging.NowPlayingMoviesPagingSource
import com.example.androidtbc.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    val nowPlayingMovies: Flow<PagingData<MovieResult>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { NowPlayingMoviesPagingSource(repository) }
    ).flow.cachedIn(viewModelScope)
}