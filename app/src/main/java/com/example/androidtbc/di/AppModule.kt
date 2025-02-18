package com.example.androidtbc.di

import android.content.Context
import com.example.androidtbc.BuildConfig
import com.example.androidtbc.data.local.AppDatabase
import com.example.androidtbc.data.local.LocalDataStore
import com.example.androidtbc.data.remote.api.AuthService
import com.example.androidtbc.data.repository.AuthRepository
import com.example.androidtbc.data.repository.AuthRepositoryImpl
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
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor() : HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return httpLoggingInterceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(logging : HttpLoggingInterceptor) : OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }



    @Provides
    @Singleton
    fun provideJson() : Json = Json {
       ignoreUnknownKeys = true
        explicitNulls = false
    }


    @Provides
    @Singleton
    fun provideRetrofit(json: Json, okHttpClient: OkHttpClient) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
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



    @Provides
    @Singleton
    fun provideAuthRepository(
        authService: AuthService,
        dataStore: LocalDataStore,
        validator: Validator
    ): AuthRepository {
        return AuthRepositoryImpl(authService, dataStore, validator)
    }

}

