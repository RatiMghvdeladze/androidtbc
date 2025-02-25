package com.example.androidtbc.presentation.moviedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.remote.dto.MovieDetailDto
import com.example.androidtbc.data.repository.MovieDetailRepository
import com.example.androidtbc.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieDetailRepository
) : ViewModel() {

    private val _movieDetails = MutableStateFlow<Resource<MovieDetailDto>>(Resource.Loading)
    val movieDetails = _movieDetails.asStateFlow()

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _movieDetails.value = Resource.Loading
            val result = repository.getMovieDetails(movieId)
            _movieDetails.value = result
        }
    }
}