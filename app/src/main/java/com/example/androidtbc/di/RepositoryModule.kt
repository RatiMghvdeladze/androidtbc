package com.example.androidtbc.di

import com.example.androidtbc.data.repository.MovieDetailRepository
import com.example.androidtbc.data.repository.MovieDetailRepositoryImpl
import com.example.androidtbc.data.repository.MovieRepository
import com.example.androidtbc.data.repository.MovieRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository

    @Binds
    @Singleton
    abstract fun provideMovieDetailRepository(movieDetailsRepositoryImpl: MovieDetailRepositoryImpl): MovieDetailRepository
}