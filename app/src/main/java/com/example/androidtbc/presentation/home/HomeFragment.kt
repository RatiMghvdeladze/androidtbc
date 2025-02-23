package com.example.androidtbc.presentation.home

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androidtbc.data.remote.dto.PostDTO
import com.example.androidtbc.data.remote.dto.StoryDTO
import com.example.androidtbc.data.remote.repository.PostAdapter
import com.example.androidtbc.data.remote.repository.StoryAdapter
import com.example.androidtbc.databinding.FragmentHomeBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val homeViewModel: HomeViewModel by viewModels()

    private val storyAdapter: StoryAdapter by lazy {
        StoryAdapter()
    }
    private val postAdapter: PostAdapter by lazy {
        PostAdapter()
    }

    override fun start() {
        setUpAdapters()
        setupSwipeRefresh()
        observer()
        loadInitialData()
    }

    private fun setUpAdapters() {
        with(binding) {
            rvStories.apply {
                adapter = storyAdapter
                setHasFixedSize(true)
                overScrollMode = View.OVER_SCROLL_NEVER
            }

            rvPosts.apply {
                adapter = postAdapter
            }
        }
    }

    private fun setupSwipeRefresh() {
        with(binding.swipeRefresh) {
            isEnabled = true
            setOnRefreshListener {
                isRefreshing = true
                homeViewModel.refresh()
            }
        }
    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    homeViewModel.stories.collectLatest { resource ->
                        handleStoriesResource(resource)
                    }
                }

                launch {
                    homeViewModel.posts.collectLatest { resource ->
                        handlePostsResource(resource)
                    }
                }
            }
        }
    }

    private fun loadInitialData() {
        homeViewModel.loadStories()
        homeViewModel.loadPosts()
    }

    private fun handleStoriesResource(resource: Resource<List<StoryDTO>>) {
        when (resource) {
            is Resource.Loading -> {
                with(binding){
                    swipeRefresh.isRefreshing = true
                    if (storyAdapter.currentList.isEmpty()) {
                        rvStories.isVisible = false
                    }
                }
            }
            is Resource.Success -> {
                with(binding) {
                    swipeRefresh.isRefreshing = false
                    rvStories.isVisible = true
                    storyAdapter.submitList(resource.data)
                }
            }
            is Resource.Error -> {
                with(binding) {
                    swipeRefresh.isRefreshing = false
                    if (storyAdapter.currentList.isEmpty()) {
                        rvStories.isVisible = false
                    }
                    showError(resource.errorMessage)
                }
            }
            is Resource.Idle -> {
            }
        }
    }

    private fun handlePostsResource(resource: Resource<List<PostDTO>>) {
        when (resource) {
            is Resource.Loading -> {
                with(binding) {
                    swipeRefresh.isRefreshing = true
                    if (postAdapter.currentList.isEmpty()) {
                        rvPosts.isVisible = false
                    }
                }
            }
            is Resource.Success -> {
                with(binding){
                    swipeRefresh.isRefreshing = false
                    rvPosts.isVisible = true
                    postAdapter.submitList(resource.data)
                }
            }
            is Resource.Error -> {
                with(binding) {
                    swipeRefresh.isRefreshing = false
                    if (postAdapter.currentList.isEmpty()) {
                        rvPosts.isVisible = false
                    }
                    showError(resource.errorMessage)
                }
            }
            is Resource.Idle -> {
            }
        }
    }


    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}