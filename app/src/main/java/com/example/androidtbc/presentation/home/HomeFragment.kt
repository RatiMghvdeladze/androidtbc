package com.example.androidtbc.presentation.home

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtbc.data.paging.UsersLoadStateAdapter
import com.example.androidtbc.data.repository.UsersAdapter
import com.example.androidtbc.databinding.FragmentHomeBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var usersAdapter: UsersAdapter
    private val args: HomeFragmentArgs by navArgs()

    override fun start() {
        setupRecyclerView()
        observeViewState()
        observeEvents()
        observeUsers()
        observeLoadStates()
        setupListeners()
    }

    private fun setupRecyclerView() {
        usersAdapter = UsersAdapter()
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = usersAdapter.withLoadStateFooter(
                footer = UsersLoadStateAdapter { usersAdapter.retry() }
            )
        }
    }

    private fun observeViewState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.viewState.collect { state ->
                    handleViewState(state)
                }
            }
        }
    }

    private fun handleViewState(state: HomeViewState) {
        binding.progressBar.isVisible = state.isLoading
        binding.tvNoUsers.isVisible = state.isEmpty
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.events.collect { event ->
                    handleEvent(event)
                }
            }
        }
    }

    private fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.NavigateToProfile -> {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToProfileFragment(event.email)
                )
            }
            is HomeEvent.ShowSnackbar -> {
                Snackbar.make(binding.root, event.message, Snackbar.LENGTH_LONG).show()
            }
            is HomeEvent.UserDataLoaded -> {
            }
        }
    }

    private fun observeUsers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.users.collect { pagingData ->
                    usersAdapter.submitData(lifecycle, pagingData)
                }
            }
        }
    }

    private fun observeLoadStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                usersAdapter.loadStateFlow.collectLatest { loadState ->
                    val isLoading = loadState.refresh is LoadState.Loading
                    val isEmpty = loadState.refresh is LoadState.NotLoading && usersAdapter.itemCount == 0

                    val errorState = loadState.source.refresh as? LoadState.Error
                        ?: loadState.source.append as? LoadState.Error
                        ?: loadState.source.prepend as? LoadState.Error

                    val errorMessage = errorState?.error?.message

                    homeViewModel.updateViewState(
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        isEmpty = isEmpty
                    )
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnGoToProfile.setOnClickListener {
            homeViewModel.processIntent(HomeIntent.NavigateToProfile)
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToProfileFragment(args.email)
            )
        }
    }
}