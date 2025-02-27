// UserRepository.kt
package com.example.androidtbc.data.repository

import com.example.androidtbc.data.remote.dto.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val usersCollection = firestore.collection("users")
    private var cachedUser: User? = null

    suspend fun saveUserInfo(name: String, phoneNumber: String, city: String): Result<Unit> {
        return try {
            val currentUser = auth.currentUser ?: return Result.failure(Exception("No authenticated user"))

            val userData = hashMapOf(
                "fullName" to name,
                "phoneNumber" to phoneNumber,
                "city" to city,
                "email" to (currentUser.email ?: "")
            )

            usersCollection.document(currentUser.uid).set(userData).await()

            // Update cache with new data
            cachedUser = User(
                email = currentUser.email ?: "",
                fullName = name,
                phoneNumber = phoneNumber,
                city = city
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserInfo(forceRefresh: Boolean = false): Result<User> {
        // Return cached user if available and not forcing refresh
        if (!forceRefresh && cachedUser != null) {
            return Result.success(cachedUser!!)
        }

        return try {
            val currentUser = auth.currentUser ?: return Result.failure(Exception("No authenticated user"))

            val document = usersCollection.document(currentUser.uid).get().await()
            if (document.exists()) {
                val user = User(
                    email = document.getString("email") ?: "",
                    fullName = document.getString("fullName") ?: "",
                    phoneNumber = document.getString("phoneNumber") ?: "",
                    city = document.getString("city") ?: ""
                )

                // Update cache
                cachedUser = user

                Result.success(user)
            } else {
                Result.failure(Exception("User data not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun clearCache() {
        cachedUser = null
    }
}