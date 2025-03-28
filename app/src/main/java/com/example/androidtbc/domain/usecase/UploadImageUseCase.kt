package com.example.androidtbc.domain.usecase

import android.graphics.Bitmap
import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.repository.FirebaseStorageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UploadImageUseCase{
    suspend operator fun invoke(bitmap: Bitmap): Flow<Resource<Unit>>
}

class UploadImageUseCaseImpl @Inject constructor(
    private val firebaseStorageRepository: FirebaseStorageRepository
) : UploadImageUseCase {
    override suspend operator fun invoke(bitmap: Bitmap): Flow<Resource<Unit>> {
        return firebaseStorageRepository.uploadImage(bitmap)
    }
}