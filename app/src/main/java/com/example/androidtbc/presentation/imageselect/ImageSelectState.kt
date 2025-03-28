package com.example.androidtbc.presentation.imageselect

import android.graphics.Bitmap
import android.net.Uri

data class ImageSelectState(
    val compressedImage: Bitmap? = null,
    val tempUri: Uri? = null,
    val isUploading: Boolean = false,
    val uploadError: String? = null,
    val uploadSuccess: Boolean = false
)