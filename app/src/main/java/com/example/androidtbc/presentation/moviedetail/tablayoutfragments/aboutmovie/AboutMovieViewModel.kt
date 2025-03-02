package com.example.androidtbc.presentation.moviedetail.tablayoutfragments.aboutmovie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.repository.MovieDetailRepository
import com.example.androidtbc.presentation.mapper.toMovieDetail
import com.example.androidtbc.presentation.model.MovieDetail
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

    private val _movieDetail = MutableStateFlow<Resource<MovieDetail>>(Resource.Loading)
    val movieDetail: StateFlow<Resource<MovieDetail>> = _movieDetail.asStateFlow()

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _movieDetail.value = Resource.Loading
            try {
                val result = repository.getMovieDetails(movieId)
                _movieDetail.value = when (result) {
                    is Resource.Success -> Resource.Success(result.data.toMovieDetail())
                    is Resource.Error -> Resource.Error(result.errorMessage)
                    else -> result as Resource<MovieDetail>
                }
            } catch (e: Exception) {
                _movieDetail.value = Resource.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}