package com.example.androidtbc.domain.repository

import android.graphics.Bitmap
import android.net.Uri

interface ImageCompressor {
    suspend fun compressImage(uri: Uri): Bitmap?
}