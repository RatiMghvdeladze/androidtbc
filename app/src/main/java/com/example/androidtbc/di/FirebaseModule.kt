package com.example.androidtbc.di

import android.content.Context
import com.example.androidtbc.data.repository.FirestoreMovieRepository
import com.example.androidtbc.data.repository.FirestoreMovieRepositoryImpl
import com.example.androidtbc.data.repository.UserRepository
import com.example.androidtbc.data.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): UserRepository = UserRepositoryImpl(firestore, auth)

    @Provides
    @Singleton
    fun provideFirestoreMovieRepository(
        db: FirebaseFirestore,
        auth: FirebaseAuth,
        @ApplicationContext context: Context
    ): FirestoreMovieRepository {
        return FirestoreMovieRepositoryImpl(db, auth, context)
    }
}