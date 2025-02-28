package com.example.androidtbc.presentation.moviedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.remote.dto.MovieDetailDto
import com.example.androidtbc.data.repository.FirestoreMovieRepository
import com.example.androidtbc.data.repository.MovieDetailRepository
import com.example.androidtbc.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieDetailRepository,
    private val firestoreRepository: FirestoreMovieRepository
) : ViewModel() {

    private val _movieDetails = MutableStateFlow<Resource<MovieDetailDto>>(Resource.Loading)
    val movieDetails: StateFlow<Resource<MovieDetailDto>> = _movieDetails

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _movieDetails.value = Resource.Loading
            val response = repository.getMovieDetails(movieId)
            _movieDetails.value = response

            checkIfMovieSaved(movieId)
        }
    }

    private fun checkIfMovieSaved(movieId: Int) {
        viewModelScope.launch {
            val isSaved = firestoreRepository.isMovieSaved(movieId)
            _isSaved.value = isSaved
        }
    }

    fun toggleSaveMovie() {
        viewModelScope.launch {
            when (val movieDetailsResource = _movieDetails.value) {
                is Resource.Success -> {
                    val movie = movieDetailsResource.data
                    val currentSaveStatus = _isSaved.value

                    if (currentSaveStatus) {
                        if (firestoreRepository.removeMovie(movie.id)) {
                            _isSaved.value = false
                        }
                    } else {
                        if (firestoreRepository.saveMovie(movie)) {
                            _isSaved.value = true
                        }
                    }

                    val actualStatus = firestoreRepository.isMovieSaved(movie.id)
                    if (_isSaved.value != actualStatus) {
                        _isSaved.value = actualStatus
                    }
                }
                else -> {}
            }
        }
    }
}