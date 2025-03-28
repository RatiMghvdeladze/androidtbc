package com.example.androidtbc.data.repository

import android.content.Context
import android.graphics.Bitmap
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.androidtbc.data.worker.ImageUploadWorker
import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.repository.FirebaseStorageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject

class FirebaseStorageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager
) : FirebaseStorageRepository {
    override suspend fun uploadImage(bitmap: Bitmap): Flow<Resource<Unit>> {
        return flow {
            try {
                val uniqueFileName = "image_${UUID.randomUUID()}.jpg"
                val file = File(context.cacheDir, uniqueFileName)

                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    out.flush()
                }

                val uploadRequest = OneTimeWorkRequest.Builder(ImageUploadWorker::class.java)
                    .setInputData(
                        workDataOf(
                            ImageUploadWorker.KEY_FILE_PATH to file.absolutePath,
                            ImageUploadWorker.KEY_FILE_NAME to uniqueFileName
                        )
                    )
                    .build()

                workManager.beginUniqueWork(
                    UPLOAD_WORKER,
                    ExistingWorkPolicy.KEEP,
                    uploadRequest
                ).enqueue()

                workManager.getWorkInfoByIdFlow(uploadRequest.id).collect { workInfo ->
                    when (workInfo?.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            emit(Resource.Success(Unit))
                        }

                        WorkInfo.State.FAILED -> {
                            val errorData =
                                workInfo.outputData.getString(ImageUploadWorker.KEY_ERROR)
                            emit(Resource.Error(errorData ?: "Upload failed"))
                        }

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unknown error"))
            }
        }
            .flowOn(Dispatchers.IO)
    }

    companion object {
        private const val UPLOAD_WORKER = "unique_image_upload_worker"
    }
}