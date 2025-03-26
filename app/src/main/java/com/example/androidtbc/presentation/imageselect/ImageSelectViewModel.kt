package com.example.androidtbc.presentation.imageselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.presentation.util.Compressor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageSelectViewModel @Inject constructor(
    private val compressor: Compressor,
) : ViewModel() {

    private val _state = MutableStateFlow(ImageSelectState())
    val state = _state.asStateFlow()

    fun onEvent(event: ImageSelectEvent) {
        when (event) {
            is ImageSelectEvent.ProcessImage -> {
                compressImage()
            }
            is ImageSelectEvent.UriCreated -> {
                _state.update { it.copy(tempUri = event.uri) }
            }

            is ImageSelectEvent.ClearImage -> {
                _state.update { it.copy(compressedImage = null, tempUri = null) }
            }

        }
    }

    private fun compressImage() {
        viewModelScope.launch(Dispatchers.IO) {
            val uri = state.value.tempUri
            uri?.let {
                val compressedBitmap = compressor.compressImage(uri)
                _state.update { it.copy(compressedImage = compressedBitmap, tempUri = null) }
            }

        }
    }

}