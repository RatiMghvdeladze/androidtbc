package com.example.androidtbc.data.repository

import com.example.androidtbc.data.remote.dto.MovieDetailDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface FirestoreMovieRepository {
    suspend fun saveMovie(movie: MovieDetailDto): Boolean
    suspend fun removeMovie(movieId: Int): Boolean
    suspend fun getAllSavedMovies(): List<MovieDetailDto>
    suspend fun isMovieSaved(movieId: Int): Boolean
    suspend fun clearAllSavedMovies(): Boolean
    suspend fun deleteSavedMovie(movieId: Int): Boolean
}

@Singleton
class FirestoreMovieRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FirestoreMovieRepository {

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: "anonymous"

    private val savedMoviesCollection
        get() = db.collection("users").document(currentUserId).collection("savedMovies")

    override suspend fun saveMovie(movie: MovieDetailDto): Boolean {
        if (auth.currentUser == null) {
            return false
        }

        savedMoviesCollection.document(movie.id.toString()).set(movie).await()
        return isMovieSaved(movie.id)
    }

    override suspend fun removeMovie(movieId: Int): Boolean {
        if (auth.currentUser == null) {
            return false
        }

        savedMoviesCollection.document(movieId.toString()).delete().await()
        return !isMovieSaved(movieId)
    }

    override suspend fun deleteSavedMovie(movieId: Int): Boolean {
        return removeMovie(movieId)
    }

    override suspend fun getAllSavedMovies(): List<MovieDetailDto> {
        return savedMoviesCollection.get().await().toObjects(MovieDetailDto::class.java)
    }

    override suspend fun isMovieSaved(movieId: Int): Boolean {
        val document = savedMoviesCollection.document(movieId.toString()).get().await()
        return document.exists()
    }

    override suspend fun clearAllSavedMovies(): Boolean {
        if (auth.currentUser == null) {
            return false
        }

        val movies = getAllSavedMovies()

        for (movie in movies) {
            savedMoviesCollection.document(movie.id.toString()).delete().await()
        }

        return getAllSavedMovies().isEmpty()
    }
}