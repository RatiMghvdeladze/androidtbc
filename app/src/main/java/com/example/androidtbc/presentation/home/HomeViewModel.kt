package com.example.androidtbc.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.androidtbc.data.paging.UserPagingSource
import com.example.androidtbc.data.remote.api.RetrofitClient

class HomeViewModel : ViewModel() {

    val users = Pager(
        config = PagingConfig(
            pageSize = 6,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            UserPagingSource(RetrofitClient.authService)
        }
    ).flow.cachedIn(viewModelScope)
}
