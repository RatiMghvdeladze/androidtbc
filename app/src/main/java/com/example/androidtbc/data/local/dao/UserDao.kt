package com.example.androidtbc.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidtbc.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)

    @Query("SELECT * FROM users_table ORDER BY id ASC")
    fun getAllUsers(): PagingSource<Int, UserEntity>

    @Query("DELETE FROM users_table")
    suspend fun clearAllUsers()

    @Query("SELECT COUNT(*) FROM users_table")
    suspend fun getUserCount(): Int

    @Query("SELECT MAX(lastUpdated) FROM users_table")
    suspend fun getLastUpdate(): Long?

    @Query("SELECT MAX(id) FROM users_table")
    suspend fun getLastUserId(): Int?
}