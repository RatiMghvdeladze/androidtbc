package com.example.androidtbc.presentation.imageselect

import android.graphics.Bitmap
import android.net.Uri

sealed class ImageSelectEvent {
    data class ImageCompressed(val bitmap: Bitmap) : ImageSelectEvent()
    data class UriCreated(val uri: Uri) : ImageSelectEvent()
    data object ImageCleared : ImageSelectEvent()
    data object UploadStarted : ImageSelectEvent()
    data object UploadCompleted : ImageSelectEvent()
    data class UploadFailed(val error: String) : ImageSelectEvent()
}
