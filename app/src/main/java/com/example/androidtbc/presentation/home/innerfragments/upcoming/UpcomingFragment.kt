package com.example.androidtbc.presentation.home.innerfragments.upcoming

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
}