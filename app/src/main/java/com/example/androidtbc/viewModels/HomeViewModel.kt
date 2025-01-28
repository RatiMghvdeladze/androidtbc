package com.example.androidtbc.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.androidtbc.RetrofitClient
import com.example.androidtbc.paging.UserPagingSource

class HomeViewModel : ViewModel() {

    val users = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            UserPagingSource(RetrofitClient.authService)
        }
    ).flow.cachedIn(viewModelScope)
}
