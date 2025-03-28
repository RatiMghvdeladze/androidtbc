package com.example.androidtbc.di

import com.example.androidtbc.domain.usecase.ImageCompressorUseCase
import com.example.androidtbc.domain.usecase.UploadImageUseCase
import com.example.androidtbc.domain.usecase.UploadImageUseCaseImpl
import com.example.androidtbc.domain.usecase.ImageCompressorUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    @Singleton
    abstract fun bindUploadImageUseCase(
        uploadImageUseCaseImpl: UploadImageUseCaseImpl
    ): UploadImageUseCase

    @Binds
    @Singleton
    abstract fun bindImageCompressorUseCase(
        imageCompressorUseCaseImpl: ImageCompressorUseCaseImpl
    ): ImageCompressorUseCase

}