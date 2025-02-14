package com.example.androidtbc.di

import com.example.androidtbc.data.remote.api.AuthService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
        return Retrofit.Builder().baseUrl("https://run.mocky.io/v3/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build()
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit) : AuthService = retrofit.create(AuthService::class.java)


}