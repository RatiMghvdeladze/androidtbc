package com.example.androidtbc.data.compressor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.androidtbc.domain.repository.ImageCompressor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class Compressor @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageCompressor {
    override suspend fun compressImage(uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            val inputBytes = context
                .contentResolver
                .openInputStream(uri)?.use { inputStream ->
                    inputStream.readBytes()
                } ?: return@withContext null

            withContext(Dispatchers.Default) {
                val bitmap = BitmapFactory.decodeByteArray(inputBytes, 0, inputBytes.size)

                var outputBytes: ByteArray
                ByteArrayOutputStream().use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, outputStream)
                    outputBytes = outputStream.toByteArray()
                }

                BitmapFactory.decodeByteArray(outputBytes, 0, outputBytes.size)

            }
        }
    }
}