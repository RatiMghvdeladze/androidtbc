package com.example.androidtbc

import android.app.Application
import androidx.room.Room

class MyApp : Application() {
    companion object{
        lateinit var dataBase: UserDataBase
    }

    override fun onCreate() {
        super.onCreate()
        dataBase = Room.databaseBuilder(
            applicationContext, UserDataBase::class.java, "my_database"
        ).build()
    }
}