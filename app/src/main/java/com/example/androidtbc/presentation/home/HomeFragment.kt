package com.example.androidtbc.presentation.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()

    private val storyAdapter: StoryAdapter by lazy {
        StoryAdapter { story ->
            handleStoryClick(story)
        }
    }

    private val postAdapter: PostAdapter by lazy {
        PostAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        start()
    }

    override fun start() {
        setupAdapters()
        setupSwipeRefresh()
        observeData()
        loadInitialData()
    }

    private fun setupAdapters() {
        binding.apply {
            // Stories setup
            rvStories.apply {
                adapter = storyAdapter
                setHasFixedSize(true)
                overScrollMode = View.OVER_SCROLL_NEVER
            }

            // Posts setup
            rvPosts.apply {
                adapter = postAdapter
                setHasFixedSize(true)
                overScrollMode = View.OVER_SCROLL_NEVER
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.apply {
            setOnRefreshListener {
                viewModel.refresh()
            }
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe stories
                launch {
                    viewModel.stories.collectLatest { resource ->
                        handleStoriesResource(resource)
                    }
                }

                // Observe posts
                launch {
                    viewModel.posts.collectLatest { resource ->
                        handlePostsResource(resource)
                    }
                }
            }
        }
    }

    private fun loadInitialData() {
        viewModel.loadStories()
        viewModel.loadPosts()
    }

    private fun handleStoriesResource(resource: Resource<List<StoryDTO>>) {
        when (resource) {
            is Resource.Loading -> {
                binding.apply {
                    if (storyAdapter.currentList.isEmpty()) {
                        rvStories.isVisible = false
                        // TODO: Show stories shimmer loading
                    }
                }
            }
            is Resource.Success -> {
                binding.apply {
                    swipeRefresh.isRefreshing = false
                    rvStories.isVisible = true
                    // TODO: Hide stories shimmer loading
                    storyAdapter.submitList(resource.data)
                }
            }
            is Resource.Error -> {
                binding.apply {
                    swipeRefresh.isRefreshing = false
                    if (storyAdapter.currentList.isEmpty()) {
                        rvStories.isVisible = false
                        // TODO: Show error view
                    }
                    showError(resource.errorMessage)
                }
            }
            is Resource.Idle -> {
                // Initial state, do nothing
            }
        }
    }

    private fun handlePostsResource(resource: Resource<List<PostDTO>>) {
        when (resource) {
            is Resource.Loading -> {
                binding.apply {
                    if (postAdapter.currentList.isEmpty()) {
                        rvPosts.isVisible = false
                        // TODO: Show posts shimmer loading
                    }
                }
            }
            is Resource.Success -> {
                binding.apply {
                    swipeRefresh.isRefreshing = false
                    rvPosts.isVisible = true
                    // TODO: Hide posts shimmer loading
                    postAdapter.submitList(resource.data)
                }
            }
            is Resource.Error -> {
                binding.apply {
                    swipeRefresh.isRefreshing = false
                    if (postAdapter.currentList.isEmpty()) {
                        rvPosts.isVisible = false
                        // TODO: Show error view
                    }
                    showError(resource.errorMessage)
                }
            }
            is Resource.Idle -> {
                // Initial state, do nothing
            }
        }
    }

    private fun handleStoryClick(story: StoryDTO) {
        // TODO: Implement story click handling (navigation or dialog)
        Toast.makeText(requireContext(), "Story clicked: ${story.title}", Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}