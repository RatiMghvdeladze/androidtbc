package com.example.androidtbc.di

import com.example.androidtbc.domain.repository.LoginRepository
import com.example.androidtbc.domain.repository.RegisterRepository
import com.example.androidtbc.domain.repository.UserRepository
import com.example.androidtbc.domain.repository.UserSessionRepository
import com.example.androidtbc.domain.usecase.auth.GetUserSessionUseCase
import com.example.androidtbc.domain.usecase.auth.LogOutUseCase
import com.example.androidtbc.domain.usecase.auth.LoginUseCase
import com.example.androidtbc.domain.usecase.auth.RegisterUseCase
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
    fun provideLoginUseCase(loginRepository: LoginRepository): LoginUseCase {
        return LoginUseCase(loginRepository)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(registerRepository: RegisterRepository): RegisterUseCase {
        return RegisterUseCase(registerRepository)
    }

    @Provides
    @Singleton
    fun provideLogoutUseCase(userSessionRepository: UserSessionRepository): LogOutUseCase {
        return LogOutUseCase(userSessionRepository)
    }

    @Provides
    @Singleton
    fun provideGetUserSessionUseCase(userSessionRepository: UserSessionRepository): GetUserSessionUseCase {
        return GetUserSessionUseCase(userSessionRepository)
    }

    @Provides
    @Singleton
    fun provideGetUsersUseCase(userRepository: UserRepository): GetUsersUseCase {
        return GetUsersUseCase(userRepository)
    }
}