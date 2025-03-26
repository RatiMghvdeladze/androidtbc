package com.example.androidtbc.presentation.imageselect

import android.net.Uri

sealed class ImageSelectEvent {
    data object ProcessImage : ImageSelectEvent()
    data class UriCreated(val uri: Uri): ImageSelectEvent()
    data object ClearImage : ImageSelectEvent()
}