package com.example.androidtbc.di

import com.example.androidtbc.data.repository.LoginRepositoryImpl
import com.example.androidtbc.data.repository.RegisterRepositoryImpl
import com.example.androidtbc.data.repository.UserRepositoryImpl
import com.example.androidtbc.data.repository.UserSessionRepositoryImpl
import com.example.androidtbc.domain.repository.LoginRepository
import com.example.androidtbc.domain.repository.RegisterRepository
import com.example.androidtbc.domain.repository.UserRepository
import com.example.androidtbc.domain.repository.UserSessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule{
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindRegisterRepository(
        registerRepositoryImpl: RegisterRepositoryImpl
    ): RegisterRepository

    @Binds
    @Singleton
    abstract fun bindUserSessionRepository(
        userSessionRepositoryImpl: UserSessionRepositoryImpl
    ): UserSessionRepository
}