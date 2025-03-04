package com.example.androidtbc.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.androidtbc.data.remote.dto.MovieDetailDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface FirestoreMovieRepository {
    suspend fun saveMovie(movie: MovieDetailDto): Boolean
    suspend fun removeMovie(movieId: Int): Boolean
    suspend fun getAllSavedMovies(): List<MovieDetailDto>
    suspend fun isMovieSaved(movieId: Int): Boolean
    suspend fun clearAllSavedMovies(): Boolean
    suspend fun deleteSavedMovie(movieId: Int): Boolean
}

class FirestoreMovieRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val context: Context
) : FirestoreMovieRepository {

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: "anonymous"

    private val savedMoviesCollection
        get() = db.collection("users").document(currentUserId).collection("savedMovies")

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                )
    }

    private suspend fun <T> retryIO(
        times: Int = 3,
        initialDelay: Long = 100,
        maxDelay: Long = 1000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(times - 1) {
            try {
                return block()
            } catch (e: FirebaseFirestoreException) {
                if (e.code != FirebaseFirestoreException.Code.UNAVAILABLE) throw e
            }
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
        return block()
    }

    override suspend fun saveMovie(movie: MovieDetailDto): Boolean {
        if (auth.currentUser == null) {
            return false
        }

        return try {
            savedMoviesCollection.document(movie.id.toString()).set(movie).await()
            true
        } catch (e: FirebaseFirestoreException) {
            if (isOnline()) {
                retryIO {
                    savedMoviesCollection.document(movie.id.toString()).set(movie).await()
                    true
                }
            } else {
                savedMoviesCollection.document(movie.id.toString()).set(movie)
                true
            }
        }
    }

    override suspend fun removeMovie(movieId: Int): Boolean {
        if (auth.currentUser == null) {
            return false
        }

        return try {
            savedMoviesCollection.document(movieId.toString()).delete().await()
            true
        } catch (e: FirebaseFirestoreException) {
            if (isOnline()) {
                retryIO {
                    savedMoviesCollection.document(movieId.toString()).delete().await()
                    true
                }
            } else {
                savedMoviesCollection.document(movieId.toString()).delete()
                true
            }
        }
    }

    override suspend fun deleteSavedMovie(movieId: Int): Boolean {
        return removeMovie(movieId)
    }

    override suspend fun getAllSavedMovies(): List<MovieDetailDto> {
        return try {
            val source = if (isOnline()) Source.SERVER else Source.CACHE
            savedMoviesCollection.get(source).await().toObjects(MovieDetailDto::class.java)
        } catch (e: FirebaseFirestoreException) {
            try {
                savedMoviesCollection.get(Source.CACHE).await().toObjects(MovieDetailDto::class.java)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    override suspend fun isMovieSaved(movieId: Int): Boolean {
        return try {
            val source = if (isOnline()) Source.SERVER else Source.CACHE
            val document = savedMoviesCollection.document(movieId.toString()).get(source).await()
            document.exists()
        } catch (e: FirebaseFirestoreException) {
            try {
                val cachedDocument = savedMoviesCollection.document(movieId.toString())
                    .get(Source.CACHE)
                    .await()
                cachedDocument.exists()
            } catch (e: Exception) {
                false
            }
        }
    }

    override suspend fun clearAllSavedMovies(): Boolean {
        if (auth.currentUser == null) {
            return false
        }

        if (!isOnline()) {
            return false
        }

        return try {
            val movies = getAllSavedMovies()

            for (movie in movies) {
                savedMoviesCollection.document(movie.id.toString()).delete().await()
            }

            true
        } catch (e: FirebaseFirestoreException) {
            false
        }
    }
}