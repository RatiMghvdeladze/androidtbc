package com.example.androidtbc.domain.repository

import android.graphics.Bitmap
import com.example.androidtbc.domain.common.Resource
import kotlinx.coroutines.flow.Flow

interface FirebaseStorageRepository {
    suspend fun uploadImage(bitmap: Bitmap): Flow<Resource<Unit>>
}