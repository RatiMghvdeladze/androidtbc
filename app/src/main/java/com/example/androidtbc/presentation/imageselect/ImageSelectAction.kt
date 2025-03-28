package com.example.androidtbc.presentation.imageselect

import android.net.Uri

sealed class ImageSelectAction {
    data object ProcessImage : ImageSelectAction()
    data class UriCreated(val uri: Uri): ImageSelectAction()
    data object ClearImage : ImageSelectAction()
    data object UploadImage : ImageSelectAction()
}