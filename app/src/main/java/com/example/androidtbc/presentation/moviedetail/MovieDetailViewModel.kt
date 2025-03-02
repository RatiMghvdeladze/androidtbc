package com.example.androidtbc.presentation.moviedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.repository.FirestoreMovieRepository
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
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieDetailRepository,
    private val firestoreRepository: FirestoreMovieRepository
) : ViewModel() {

    private val _movieDetails = MutableStateFlow<Resource<MovieDetail>>(Resource.Loading)
    val movieDetails: StateFlow<Resource<MovieDetail>> = _movieDetails

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    private var cachedMovieDetail: MovieDetail? = null

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _movieDetails.value = Resource.Loading

            when (val response = repository.getMovieDetails(movieId)) {
                is Resource.Success -> {
                    val movieDetail = response.data.toMovieDetail()
                    cachedMovieDetail = movieDetail
                    _movieDetails.value = Resource.Success(movieDetail)
                }
                is Resource.Error -> {
                    _movieDetails.value = Resource.Error(response.errorMessage)
                }
                else -> {
                    _movieDetails.value = Resource.Error("Unknown error occurred")
                }
            }

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
            cachedMovieDetail?.let { movie ->
                val currentSaveStatus = _isSaved.value

                if (currentSaveStatus) {
                    if (firestoreRepository.removeMovie(movie.id)) {
                        _isSaved.value = false
                    }
                } else {
                    // We need the original DTO for saving to Firestore
                    // This is where we would ideally have a reverse mapper
                    when (val response = repository.getMovieDetails(movie.id)) {
                        is Resource.Success -> {
                            if (firestoreRepository.saveMovie(response.data)) {
                                _isSaved.value = true
                            }
                        }
                        else -> {
                            // If we can't get fresh data, try with cached data if possible
                        }
                    }
                }

                // Verify the actual saved status
                val actualStatus = firestoreRepository.isMovieSaved(movie.id)
                if (_isSaved.value != actualStatus) {
                    _isSaved.value = actualStatus
                }
            }
        }
    }
}