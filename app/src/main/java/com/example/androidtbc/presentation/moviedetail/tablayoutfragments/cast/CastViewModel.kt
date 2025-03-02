package com.example.androidtbc.presentation.moviedetail.tablayoutfragments.cast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.repository.MovieRepository
import com.example.androidtbc.presentation.mapper.toCast
import com.example.androidtbc.presentation.model.Cast
import com.example.androidtbc.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CastViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _castDetails = MutableStateFlow<Resource<Cast>>(Resource.Loading)
    val castDetails: StateFlow<Resource<Cast>> = _castDetails

    fun getMovieCast(movieId: Int) {
        viewModelScope.launch {
            _castDetails.value = Resource.Loading
            val result = repository.getMovieCast(movieId)
            _castDetails.value = when (result) {
                is Resource.Success -> Resource.Success(result.data.toCast())
                is Resource.Error -> Resource.Error(result.errorMessage)
                Resource.Loading -> Resource.Loading
                else -> Resource.Error("Unknown error")
            }
        }
    }
}