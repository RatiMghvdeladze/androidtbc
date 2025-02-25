package com.example.androidtbc.presentation.home.innerfragments.nowplaying

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.androidtbc.data.remote.dto.MovieResult
import com.example.androidtbc.databinding.FragmentNowPlayingBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.home.HomeFragmentDirections
import com.example.androidtbc.presentation.home.innerfragments.nowplaying.adapter.NowPlayingAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NowPlayingFragment : BaseFragment<FragmentNowPlayingBinding>(FragmentNowPlayingBinding::inflate) {

    private val viewModel: NowPlayingViewModel by viewModels()
    private val adapter: NowPlayingAdapter by lazy {
        NowPlayingAdapter { movie: MovieResult ->
            navigateToDetailScreen(movie)
        }
    }

    private fun navigateToDetailScreen(movie: MovieResult) {
        // Navigate using the parent fragment's action
        val action = HomeFragmentDirections.actionHomeFragmentToMovieDetailFragment(movie.id)
        // Get the parent NavController
        findNavController().navigate(action)
    }


    override fun start() {
        setUpRV()
        collectMovies()
        setupLoadingState()
    }

    private fun setUpRV() {
        with(binding) {
            rvNowPlaying.adapter = adapter
        }
    }

    private fun collectMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.nowPlayingMovies.collectLatest {
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