package com.example.androidtbc.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ViewModelFactory<T : ViewModel> (
    private val viewModelCreator: () -> T) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModelCreator() as T
    }

}