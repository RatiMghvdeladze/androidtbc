package com.example.androidtbc.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.data.remote.dto.PostDTO
import com.example.androidtbc.data.remote.dto.StoryDTO
import com.example.androidtbc.data.remote.repository.HomeRepository
import com.example.androidtbc.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _stories = MutableStateFlow<Resource<List<StoryDTO>>>(Resource.Idle)
    val stories: StateFlow<Resource<List<StoryDTO>>> = _stories

    private val _posts = MutableStateFlow<Resource<List<PostDTO>>>(Resource.Idle)
    val posts: StateFlow<Resource<List<PostDTO>>> = _posts

    fun loadStories() {
        viewModelScope.launch {
            _stories.value = Resource.Loading
            _stories.value = repository.getStories()
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            _posts.value = Resource.Loading
            _posts.value = repository.getPosts()
        }
    }

    fun refresh() {
        loadStories()
        loadPosts()
    }
}