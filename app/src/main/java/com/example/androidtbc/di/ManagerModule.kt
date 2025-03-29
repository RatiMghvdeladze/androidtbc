package com.example.androidtbc.di

import com.example.androidtbc.domain.manager.AccountManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {

    @Provides
    @Singleton
    fun provideAccountManager(): AccountManager = AccountManager()
}