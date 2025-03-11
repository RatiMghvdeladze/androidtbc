package com.example.androidtbc.di

import com.example.androidtbc.domain.repository.AuthRepository
import com.example.androidtbc.domain.repository.UserRepository
import com.example.androidtbc.domain.usecase.auth.GetUserSessionUseCase
import com.example.androidtbc.domain.usecase.auth.LogOutUseCase
import com.example.androidtbc.domain.usecase.auth.LoginUseCase
import com.example.androidtbc.domain.usecase.auth.RegisterUseCase
import com.example.androidtbc.domain.usecase.auth.SessionManager
import com.example.androidtbc.domain.usecase.user.GetUsersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(authRepository: AuthRepository): RegisterUseCase {
        return RegisterUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideLogoutUseCase(authRepository: AuthRepository): LogOutUseCase {
        return LogOutUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideGetUserSessionUseCase(authRepository: AuthRepository): GetUserSessionUseCase {
        return GetUserSessionUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideSessionManager(authRepository: AuthRepository): SessionManager {
        return SessionManager(authRepository)
    }

    @Provides
    @Singleton
    fun provideGetUsersUseCase(userRepository: UserRepository): GetUsersUseCase {
        return GetUsersUseCase(userRepository)
    }
}