package com.example.androidtbc.di

import com.example.androidtbc.data.compressor.Compressor
import com.example.androidtbc.domain.repository.ImageCompressor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CompressorModule {

    @Binds
    @Singleton
    abstract fun bindImageCompressor(
        compressor: Compressor
    ): ImageCompressor
}