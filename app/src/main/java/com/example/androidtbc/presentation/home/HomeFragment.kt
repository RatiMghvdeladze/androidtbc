package com.example.androidtbc.presentation.home

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtbc.databinding.FragmentHomeBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.extension.launchLatest
import com.example.androidtbc.presentation.extension.showSnackbar
import com.example.androidtbc.presentation.home.adapter.UsersAdapter
import com.example.androidtbc.presentation.home.adapter.UsersLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var usersAdapter: UsersAdapter

    override fun start() {
        setUpRV()
        observeViewState()
        observeEvents()
        observeUsers()
        observeLoadStates()
        setupListeners()
    }

    private fun setUpRV() {
        usersAdapter = UsersAdapter()
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = usersAdapter.withLoadStateFooter(
                footer = UsersLoadStateAdapter { usersAdapter.retry() }
            )
        }
    }

    private fun observeViewState() {
        launchLatest(homeViewModel.state) { state ->
            handleViewState(state)
        }
    }

    private fun handleViewState(state: HomeState) {
        binding.progressBar.isVisible = state.isLoading
        binding.tvNoUsers.isVisible = state.isEmpty
    }

    private fun observeEvents() {
        launchLatest(homeViewModel.events) { event ->
            handleEvent(event)
        }
    }

    private fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.NavigateToProfile -> {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToProfileFragment()
                )
            }
            is HomeEvent.ShowSnackbar -> {
                binding.root.showSnackbar(event.message)
            }
            is HomeEvent.UserDataLoaded -> {
            }
            else -> {
            }
        }
    }

    private fun observeUsers() {
        launchLatest(homeViewModel.users) { pagingData ->
            usersAdapter.submitData(lifecycle, pagingData)
        }
    }

    private fun observeLoadStates() {
        launchLatest(usersAdapter.loadStateFlow) { loadState ->
            val isLoading = loadState.refresh is LoadState.Loading
            val isEmpty = loadState.refresh is LoadState.NotLoading && usersAdapter.itemCount == 0
            val errorState = loadState.source.refresh as? LoadState.Error
                ?: loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
            val errorMessage = errorState?.error?.message

            homeViewModel.onEvent(HomeEvent.UpdateViewState(
                isLoading = isLoading,
                errorMessage = errorMessage,
                isEmpty = isEmpty
            ))
        }
    }

    private fun setupListeners() {
        binding.btnGoToProfile.setOnClickListener {
            homeViewModel.onEvent(HomeEvent.NavigateToProfile)
        }
    }
}