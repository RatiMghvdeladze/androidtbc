package com.example.androidtbc.di

import com.example.androidtbc.domain.usecase.GetCategoriesUseCase
import com.example.androidtbc.domain.usecase.GetCategoriesUseCaseImpl
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
    abstract fun bindGetCategoriesUseCase(
        getCategoriesUseCaseImpl: GetCategoriesUseCaseImpl
    ): GetCategoriesUseCase

}