package com.example.androidtbc.data

import android.app.Application
import androidx.room.Room

class MyApp : Application() {
    companion object {
        private lateinit var instance: MyApp
        fun getDatabase(): UserDatabase = instance.database
    }

    private lateinit var database: UserDatabase

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = Room.databaseBuilder(
            applicationContext,
            UserDatabase::class.java,
            "my_database"
        ).build()
    }
}