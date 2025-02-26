package com.example.androidtbc.presentation.moviedetail.tablayoutfragments.cast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.remote.dto.CastDto
import com.example.androidtbc.data.repository.MovieRepository
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

    private val _castDetails = MutableStateFlow<Resource<CastDto>>(Resource.Loading)
    val castDetails: StateFlow<Resource<CastDto>> = _castDetails

    fun getMovieCast(movieId: Int) {
        viewModelScope.launch {
            _castDetails.value = Resource.Loading
            _castDetails.value = repository.getMovieCast(movieId)
        }
    }
}