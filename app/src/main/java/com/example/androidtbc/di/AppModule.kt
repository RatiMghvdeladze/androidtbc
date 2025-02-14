package com.example.androidtbc.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.androidtbc.AuthService
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
        return Retrofit.Builder().baseUrl("https://run.mocky.io/v3/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build()
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit) : AuthService = retrofit.create(AuthService::class.java)



    @Provides
    @Singleton
    fun provideGlide(@ApplicationContext context: Context): RequestManager = Glide.with(context)


}