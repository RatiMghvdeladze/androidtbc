package com.example.androidtbc.di

import com.example.androidtbc.data.local.DataStoreManagerImpl
import com.example.androidtbc.domain.datastore.DataStoreManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Binds
    @Singleton
    abstract fun bindDataStoreManager(
        dataStoreManagerImpl: DataStoreManagerImpl
    ): DataStoreManager
}