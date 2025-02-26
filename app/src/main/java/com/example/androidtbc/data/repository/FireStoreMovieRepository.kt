package com.example.androidtbc.data.repository

import android.util.Log
import com.example.androidtbc.data.remote.dto.MovieDetailDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreMovieRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Make this a computed property to always get the most current user
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: "anonymous"

    // Make this a computed property too
    private val savedMoviesCollection
        get() = db.collection("users").document(currentUserId).collection("savedMovies")

    // Consider adding this check in FirestoreMovieRepository
    suspend fun saveMovie(movie: MovieDetailDto): Boolean {
        if (auth.currentUser == null) {
            Log.e("FirestoreRepo", "Cannot save movie: User not logged in")
            return false
        }

        return try {
            Log.d("FirestoreRepo", "Saving movie: ${movie.title} with ID ${movie.id} for user $currentUserId")
            savedMoviesCollection.document(movie.id.toString()).set(movie).await()

            // Verify if the save was successful
            val saved = isMovieSaved(movie.id)
            Log.d("FirestoreRepo", "Movie saved, verification: $saved")
            saved
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Error saving movie: ${e.message}", e)
            false
        }
    }

    suspend fun removeMovie(movieId: Int): Boolean {
        if (auth.currentUser == null) {
            Log.e("FirestoreRepo", "Cannot remove movie: User not logged in")
            return false
        }

        return try {
            Log.d("FirestoreRepo", "Attempting to remove movie with ID $movieId for user $currentUserId")
            savedMoviesCollection.document(movieId.toString()).delete().await()
            Log.d("FirestoreRepo", "Successfully removed movie with ID $movieId")
            true
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Error removing movie $movieId: ${e.message}", e)
            false
        }
    }

    // In FirestoreMovieRepository
    suspend fun getAllSavedMovies(): List<MovieDetailDto> {
        return try {
            val result = savedMoviesCollection.get().await().toObjects(MovieDetailDto::class.java)
            Log.d("FirestoreRepo", "Retrieved ${result.size} saved movies")
            result
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Error getting saved movies: ${e.javaClass.simpleName} - ${e.message}", e)
            emptyList()
        }
    }

    suspend fun isMovieSaved(movieId: Int): Boolean {
        return try {
            val document = savedMoviesCollection.document(movieId.toString()).get().await()
            document.exists()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun clearAllSavedMovies(): Boolean {
        if (auth.currentUser == null) {
            Log.e("FirestoreRepo", "Cannot clear movies: User not logged in")
            return false
        }

        return try {
            Log.d("FirestoreRepo", "Attempting to clear all saved movies for user $currentUserId")

            // Get all saved movies first
            val movies = getAllSavedMovies()

            // Delete each movie document one by one
            // Using batch or transaction can be more efficient for larger collections
            var successful = true
            for (movie in movies) {
                try {
                    savedMoviesCollection.document(movie.id.toString()).delete().await()
                } catch (e: Exception) {
                    Log.e("FirestoreRepo", "Error deleting movie ${movie.id}: ${e.message}")
                    successful = false
                }
            }

            Log.d("FirestoreRepo", "Successfully cleared all saved movies")
            successful
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Error clearing saved movies: ${e.message}", e)
            false
        }
    }
}