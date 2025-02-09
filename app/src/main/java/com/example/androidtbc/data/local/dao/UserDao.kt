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

    @Query("SELECT * FROM users_table")
    fun getAllUsers(): PagingSource<Int, UserEntity>

    @Query("DELETE FROM users_table")
    suspend fun clearAllUsers()
}
