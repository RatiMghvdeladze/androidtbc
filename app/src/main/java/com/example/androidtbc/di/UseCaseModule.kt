package com.example.androidtbc.di

import com.example.androidtbc.domain.usecase.auth.CheckRememberMeUseCase
import com.example.androidtbc.domain.usecase.auth.CheckRememberMeUseCaseImpl
import com.example.androidtbc.domain.usecase.auth.CheckSessionActiveUseCase
import com.example.androidtbc.domain.usecase.auth.CheckSessionActiveUseCaseImpl
import com.example.androidtbc.domain.usecase.auth.ClearTokenUseCase
import com.example.androidtbc.domain.usecase.auth.ClearTokenUseCaseImpl
import com.example.androidtbc.domain.usecase.auth.CompleteLogoutUseCase
import com.example.androidtbc.domain.usecase.auth.CompleteLogoutUseCaseImpl
import com.example.androidtbc.domain.usecase.auth.FetchUserEmailUseCase
import com.example.androidtbc.domain.usecase.auth.FetchUserEmailUseCaseImpl
import com.example.androidtbc.domain.usecase.auth.FetchUserSessionUseCase
import com.example.androidtbc.domain.usecase.auth.FetchUserSessionUseCaseImpl
import com.example.androidtbc.domain.usecase.auth.LoginUseCase
import com.example.androidtbc.domain.usecase.auth.LoginUseCaseImpl
import com.example.androidtbc.domain.usecase.auth.RegisterUseCase
import com.example.androidtbc.domain.usecase.auth.RegisterUseCaseImpl
import com.example.androidtbc.domain.usecase.user.GetUsersUseCase
import com.example.androidtbc.domain.usecase.user.GetUsersUseCaseImpl
import com.example.androidtbc.domain.usecase.validation.ValidateEmailUseCase
import com.example.androidtbc.domain.usecase.validation.ValidateEmailUseCaseImpl
import com.example.androidtbc.domain.usecase.validation.ValidatePasswordUseCase
import com.example.androidtbc.domain.usecase.validation.ValidatePasswordUseCaseImpl
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
    abstract fun bindValidatePasswordUseCase(
        validatePasswordUseCaseImpl: ValidatePasswordUseCaseImpl): ValidatePasswordUseCase

    @Binds
    @Singleton
    abstract fun bindValidateEmailUseCase(
        validateEmailUseCaseImpl: ValidateEmailUseCaseImpl): ValidateEmailUseCase

    @Binds
    @Singleton
    abstract fun bindRegisterUseCase(
        registerUseCaseImpl: RegisterUseCaseImpl
    ): RegisterUseCase

    @Binds
    @Singleton
    abstract fun bindLoginUseCase(
        loginUseCaseImpl: LoginUseCaseImpl
    ) : LoginUseCase

    @Binds
    @Singleton
    abstract fun bindFetchUserSessionUseCase(
        fetchUserSessionUseCaseImpl: FetchUserSessionUseCaseImpl
    ) : FetchUserSessionUseCase

    @Binds
    @Singleton
    abstract fun bindFetchUserEmailUseCase(
        fetchUserEmailUseCaseImpl: FetchUserEmailUseCaseImpl
    ) : FetchUserEmailUseCase

    @Binds
    @Singleton
    abstract fun bindCompleteLogOutUseCase(
        completeLogoutUseCaseImpl: CompleteLogoutUseCaseImpl
    ) : CompleteLogoutUseCase

    @Binds
    @Singleton
    abstract fun bindClearTokenUseCase(
        clearTokenUseCaseImpl: ClearTokenUseCaseImpl
    ) : ClearTokenUseCase

    @Binds
    @Singleton
    abstract fun bindCheckSessionActiveUseCase(
        checkSessionActiveUseCaseImpl: CheckSessionActiveUseCaseImpl
    ) : CheckSessionActiveUseCase

    @Binds
    @Singleton
    abstract fun bindGetUsersUseCase(
        getUsersUseCaseImpl: GetUsersUseCaseImpl
    ) : GetUsersUseCase

    @Binds
    @Singleton
    abstract fun bindCheckRememberMeUseCase(
        checkRememberMeUseCaseImpl: CheckRememberMeUseCaseImpl
    ) : CheckRememberMeUseCase


}