package com.example.androidtbc.presentation.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.androidtbc.data.local.AppDatabase
import com.example.androidtbc.data.remote.api.RetrofitClient
import com.example.androidtbc.data.repository.UserRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserRepository(
        application,
        RetrofitClient.authService,
        AppDatabase.getInstance(application)
    )

    val users = repository.getUsers().cachedIn(viewModelScope)
}
