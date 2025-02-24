package com.example.androidtbc.presentation.home.innerfragments.upcoming

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.example.androidtbc.databinding.FragmentUpcomingBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.home.innerfragments.upcoming.adapter.UpcomingMovieAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpcomingFragment : BaseFragment<FragmentUpcomingBinding>(FragmentUpcomingBinding::inflate) {
    private val viewModel: UpcomingViewModel by viewModels()
    private val adapter: UpcomingMovieAdapter by lazy {
        UpcomingMovieAdapter()
    }


    override fun start() {
        setUpRV()
        collectMovies()
        setupLoadingState()
    }

    private fun setUpRV() {
        with(binding) {
            rvUpcoming.adapter = adapter
        }
    }

    private fun collectMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.upcomingMovies.collectLatest {
                    adapter.submitData(it)
                }
            }
        }
    }

    private fun setupLoadingState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { loadState ->
                    binding.progressBar.visibility = when {
                        loadState.refresh is LoadState.Loading -> View.VISIBLE
                        loadState.append is LoadState.Loading -> View.VISIBLE
                        loadState.prepend is LoadState.Loading -> View.VISIBLE
                        else -> View.GONE
                    }

                    val errorState = when {
                        loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                        loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                        loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                        else -> null
                    }

                    errorState?.let {
                    }
                }
            }
        }
    }
}