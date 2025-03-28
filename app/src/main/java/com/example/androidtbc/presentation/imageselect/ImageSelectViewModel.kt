package com.example.androidtbc.presentation.imageselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.usecase.ImageCompressorUseCase
import com.example.androidtbc.domain.usecase.UploadImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageSelectViewModel @Inject constructor(
    private val imageCompressorUseCase: ImageCompressorUseCase,
    private val uploadImageUseCase: UploadImageUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ImageSelectState())
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<ImageSelectEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    fun onAction(action: ImageSelectAction) {
        when (action) {
            is ImageSelectAction.ProcessImage -> {
                compressImage()
            }
            is ImageSelectAction.UriCreated -> {
                _state.update {
                    it.copy(tempUri = action.uri, uploadSuccess = false, uploadError = null)
                }
                sendEvent(ImageSelectEvent.UriCreated(action.uri))
            }
            is ImageSelectAction.ClearImage -> {
                _state.update {
                    it.copy(
                        compressedImage = null,
                        tempUri = null,
                        uploadSuccess = false,
                        uploadError = null,
                        isUploading = false
                    )
                }
                sendEvent(ImageSelectEvent.ImageCleared)
            }
            is ImageSelectAction.UploadImage -> {
                uploadImage()
            }
        }
    }

    private fun compressImage() {
        viewModelScope.launch {
            state.value.tempUri?.let { uri ->
                val compressedBitmap = imageCompressorUseCase(uri)
                _state.update { it.copy(compressedImage = compressedBitmap, tempUri = null) }
                compressedBitmap?.let { ImageSelectEvent.ImageCompressed(it) }
                    ?.let { sendEvent(it) }
            }
        }
    }

    private fun uploadImage() {
        val bitmap = state.value.compressedImage ?: return

        _state.update { it.copy(isUploading = true, uploadError = null, uploadSuccess = false) }
        sendEvent(ImageSelectEvent.UploadStarted)

        viewModelScope.launch {
                uploadImageUseCase(bitmap).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _state.update { it.copy(isUploading = resource.isLoading) }
                        }
                        is Resource.Success -> {
                            _state.update { it.copy(isUploading = false, uploadSuccess = true) }
                            sendEvent(ImageSelectEvent.UploadCompleted)
                        }
                        is Resource.Error -> {
                            _state.update { it.copy(isUploading = false, uploadError = resource.errorMessage) }
                            sendEvent(ImageSelectEvent.UploadFailed(resource.errorMessage))
                        }
                    }
                }
        }
    }

    private fun sendEvent(event: ImageSelectEvent) {
        viewModelScope.launch {
            _eventChannel.send(event)
        }
    }
}
