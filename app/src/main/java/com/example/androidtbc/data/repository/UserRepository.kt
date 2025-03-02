// FirebaseUserRepository.kt
package com.example.androidtbc.data.repository

import com.example.androidtbc.data.remote.dto.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    suspend fun saveUserInfo(name: String, phoneNumber: String, city: String): Result<Unit>
    suspend fun getUserInfo(forceRefresh: Boolean = false): Result<User>
    fun clearCache()
}

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserRepository {

    private val usersCollection = firestore.collection("users")
    private var cachedUser: User? = null

    override suspend fun saveUserInfo(name: String, phoneNumber: String, city: String): Result<Unit> {
        val currentUser = auth.currentUser ?: return Result.failure(Exception("No authenticated user"))

        val userData = hashMapOf(
            "fullName" to name,
            "phoneNumber" to phoneNumber,
            "city" to city,
            "email" to (currentUser.email ?: "")
        )

        usersCollection.document(currentUser.uid).set(userData).await()

        cachedUser = User(
            email = currentUser.email ?: "",
            fullName = name,
            phoneNumber = phoneNumber,
            city = city
        )

        return Result.success(Unit)
    }

    override suspend fun getUserInfo(forceRefresh: Boolean): Result<User> {
        if (!forceRefresh && cachedUser != null) {
            return Result.success(cachedUser!!)
        }

        val currentUser = auth.currentUser ?: return Result.failure(Exception("No authenticated user"))

        val document = usersCollection.document(currentUser.uid).get().await()
        if (document.exists()) {
            val user = User(
                email = document.getString("email") ?: "",
                fullName = document.getString("fullName") ?: "",
                phoneNumber = document.getString("phoneNumber") ?: "",
                city = document.getString("city") ?: ""
            )

            cachedUser = user

            return Result.success(user)
        } else {
            return Result.failure(Exception("User data not found"))
        }
    }

    override fun clearCache() {
        cachedUser = null
    }
}