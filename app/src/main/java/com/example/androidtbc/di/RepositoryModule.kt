package com.example.androidtbc.di

import com.example.androidtbc.data.remote.api.ApiService
import com.example.androidtbc.data.repository.AccountRepositoryImpl
import com.example.androidtbc.data.utils.ApiHelper
import com.example.androidtbc.domain.repository.AccountRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideAccountRepository(
        apiService: ApiService,
        apiHelper: ApiHelper
    ): AccountRepository = AccountRepositoryImpl(apiService, apiHelper)
}