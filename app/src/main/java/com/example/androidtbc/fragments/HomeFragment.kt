package com.example.androidtbc.fragments

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtbc.databinding.FragmentHomeBinding
import com.example.androidtbc.paging.UsersAdapter
import com.example.androidtbc.paging.UsersLoadStateAdapter
import com.example.androidtbc.viewModels.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val homeViewModel: HomeViewModel by viewModels()
    private val usersAdapter = UsersAdapter()
    private val args: HomeFragmentArgs by navArgs()

    override fun start() {
        setUpRv()
        observeUsers()
        setupListeners()
    }

    private fun setUpRv() {
        with(binding.rvUsers) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = usersAdapter.withLoadStateFooter(
                footer = UsersLoadStateAdapter { usersAdapter.retry() }
            )
        }
    }
    private fun observeUsers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.users.collect { pagingData ->
                    usersAdapter.submitData(pagingData)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                usersAdapter.loadStateFlow.collect { loadState ->
                    binding.progressBar.isVisible = loadState.refresh is LoadState.Loading
                    binding.tvNoUsers.isVisible = loadState.refresh is LoadState.NotLoading && usersAdapter.itemCount == 0

                    val errorState = loadState.source.refresh as? LoadState.Error
                        ?: loadState.source.append as? LoadState.Error
                        ?: loadState.source.prepend as? LoadState.Error
                    errorState?.let {
                        Snackbar.make(
                            binding.root,
                            "Error: ${it.error.message}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        with(binding) {
            btnGoToProfile.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment(args.email))
            }
        }
    }
}
