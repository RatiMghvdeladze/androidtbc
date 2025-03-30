package com.example.androidtbc.di

import com.example.androidtbc.domain.manager.AccountManager
import com.example.androidtbc.domain.repository.AccountRepository
import com.example.androidtbc.domain.usecase.GetAccountsUseCase
import com.example.androidtbc.domain.usecase.GetExchangeRateUseCase
import com.example.androidtbc.domain.usecase.TransferMoneyUseCase
import com.example.androidtbc.domain.usecase.ValidateAccountUseCase
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
    fun provideGetAccountsUseCase(repository: AccountRepository, accountManager: AccountManager): GetAccountsUseCase {
        return GetAccountsUseCase(repository, accountManager)
    }

    @Provides
    @Singleton
    fun provideValidateAccountUseCase(repository: AccountRepository): ValidateAccountUseCase {
        return ValidateAccountUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetExchangeRateUseCase(repository: AccountRepository): GetExchangeRateUseCase {
        return GetExchangeRateUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideTransferMoneyUseCase(
        accountManager: AccountManager,
        getExchangeRateUseCase: GetExchangeRateUseCase
    ): TransferMoneyUseCase {
        return TransferMoneyUseCase(accountManager, getExchangeRateUseCase)
    }
}