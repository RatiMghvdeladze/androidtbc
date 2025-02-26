package com.example.androidtbc.presentation.moviedetail.tablayoutfragments.aboutmovie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.remote.dto.MovieDetailDto
import com.example.androidtbc.data.repository.MovieDetailRepository
import com.example.androidtbc.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutMovieViewModel @Inject constructor(
    private val repository: MovieDetailRepository
) : ViewModel() {

    private val _movieDetail = MutableStateFlow<Resource<MovieDetailDto>>(Resource.Loading)
    val movieDetail: StateFlow<Resource<MovieDetailDto>> = _movieDetail.asStateFlow()

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _movieDetail.value = Resource.Loading
            try {
                val result = repository.getMovieDetails(movieId)
                _movieDetail.value = result
            } catch (e: Exception) {
                _movieDetail.value = Resource.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}