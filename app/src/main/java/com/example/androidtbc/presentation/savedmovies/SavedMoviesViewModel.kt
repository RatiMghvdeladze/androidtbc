package com.example.androidtbc.presentation.savedmovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.repository.FirestoreMovieRepository
import com.example.androidtbc.presentation.mapper.toMovieUIList
import com.example.androidtbc.presentation.model.MovieUI
import com.example.androidtbc.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedMoviesViewModel @Inject constructor(
    private val firestoreRepository: FirestoreMovieRepository
) : ViewModel() {

    private val _savedMovies = MutableStateFlow<Resource<List<MovieUI>>>(Resource.Loading)
    val savedMovies: StateFlow<Resource<List<MovieUI>>> = _savedMovies

    fun fetchSavedMovies() {
        viewModelScope.launch {
            _savedMovies.value = Resource.Loading
            try {
                val movies = firestoreRepository.getAllSavedMovies()
                _savedMovies.value = Resource.Success(movies.toMovieUIList())
            } catch (e: Exception) {
                _savedMovies.value = Resource.Error("Failed to fetch saved movies: ${e.message}")
            }
        }
    }

    fun clearAllSavedMovies() {
        viewModelScope.launch {
            val success = firestoreRepository.clearAllSavedMovies()
            if (success) {
                fetchSavedMovies()
            } else {
                _savedMovies.value = Resource.Error("Failed to clear saved movies")
            }
        }
    }

    fun deleteMovie(movieId: Int) {
        viewModelScope.launch {
            val success = firestoreRepository.deleteSavedMovie(movieId)
            if (success) {
                fetchSavedMovies()
            } else {
                _savedMovies.value = Resource.Error("Failed to delete movie")
            }
        }
    }
}