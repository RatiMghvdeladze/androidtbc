package com.example.androidtbc.di

import android.content.Context
import androidx.work.WorkManager
import com.example.androidtbc.data.repository.FirebaseStorageRepositoryImpl
import com.example.androidtbc.domain.repository.FirebaseStorageRepository
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideStorageRepository(
        workManager: WorkManager,
        @ApplicationContext context: Context
    ): FirebaseStorageRepository {
        return FirebaseStorageRepositoryImpl(context, workManager)
    }
}