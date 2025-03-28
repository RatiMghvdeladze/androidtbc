package com.example.androidtbc.data.worker

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.google.firebase.storage.FirebaseStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

@HiltWorker
class ImageUploadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {
    private val firebaseStorage = FirebaseStorage.getInstance()

    override suspend fun doWork(): Result {
        val filePath = inputData.getString(KEY_FILE_PATH) ?: return Result.failure(createErrorData("No file path provided"))
        val customFileName = inputData.getString(KEY_FILE_NAME) ?: return Result.failure(createErrorData("No filename provided"))

        val file = File(filePath)

        return withContext(Dispatchers.IO) {
            try {
                val storageRef = firebaseStorage.reference.child("images/$customFileName")
                storageRef.putFile(Uri.fromFile(file)).await()
                file.delete()
                Result.success()
            } catch (e: Exception) {
                Result.failure(createErrorData(e.message ?: "Upload failed"))
            }
        }
    }

    private fun createErrorData(errorMessage: String): Data {
        return Data.Builder().putString(KEY_ERROR, errorMessage).build()
    }

    companion object {
        const val KEY_FILE_PATH = "upload_file_path"
        const val KEY_FILE_NAME = "upload_file_name"
        const val KEY_ERROR = "upload_error_key"
    }
}
