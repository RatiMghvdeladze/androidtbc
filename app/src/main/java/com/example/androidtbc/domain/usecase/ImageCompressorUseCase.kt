package com.example.androidtbc.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.example.androidtbc.domain.repository.ImageCompressor
import javax.inject.Inject

interface ImageCompressorUseCase {
    suspend operator fun invoke(uri: Uri): Bitmap?
}

class ImageCompressorUseCaseImpl @Inject constructor(
    private val imageCompressor: ImageCompressor
) : ImageCompressorUseCase{
    override suspend operator fun invoke(uri: Uri): Bitmap? {
        return imageCompressor.compressImage(uri)
    }
}