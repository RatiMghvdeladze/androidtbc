package com.example.androidtbc.di

import com.example.androidtbc.data.repository.AuthRepositoryImpl
import com.example.androidtbc.data.repository.UserRepositoryImpl
import com.example.androidtbc.domain.repository.AuthRepository
import com.example.androidtbc.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}