package com.example.androidtbc.di

import android.content.Context
import com.example.androidtbc.data.local.AppDatabase
import com.example.androidtbc.data.local.LocalDataStore
import com.example.androidtbc.data.remote.api.AuthService
import com.example.androidtbc.data.repository.UserRepository
import com.example.androidtbc.data.repository.UsersAdapter
import com.example.androidtbc.utils.Validator
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {



    @Provides
    @Singleton
    fun provideJson() : Json = Json {
       ignoreUnknownKeys = true
        explicitNulls = false
    }


    @Provides
    @Singleton
    fun provideRetrofit(json: Json) : Retrofit {
        return Retrofit.Builder().baseUrl("https://reqres.in/api/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build()
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit) : AuthService = retrofit.create(AuthService::class.java)








    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase) = appDatabase.userDao()


    @Provides
    @Singleton
    fun provideUserRepository(
        authService: AuthService,
        appDatabase: AppDatabase
    ): UserRepository {
        return UserRepository(authService, appDatabase)
    }





    @Provides
    @Singleton
    fun provideLocalDataStore(@ApplicationContext context: Context): LocalDataStore {
        return LocalDataStore(context)
    }






    @Provides
    @Singleton
    fun provideValidator() : Validator = Validator()

    @Provides
    @Singleton
    fun provideUserAdapter() : UsersAdapter = UsersAdapter()

}

