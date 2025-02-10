package com.example.androidtbc.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidtbc.data.local.entity.UserRemoteKeys

@Dao
interface UserRemoteKeysDao {
    @Query("SELECT * FROM user_remote_keys ORDER BY userId DESC LIMIT 1")
    suspend fun getLastRemoteKey(): UserRemoteKeys?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<UserRemoteKeys>)

    @Query("DELETE FROM user_remote_keys")
    suspend fun clearAllRemoteKeys()
}
