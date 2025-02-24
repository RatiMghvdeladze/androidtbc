package com.example.androidtbc.presentation.home.innerfragments.toprated

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androidtbc.databinding.FragmentTopRatedBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.home.innerfragments.toprated.adapter.TopRatedMovieAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TopRatedFragment : BaseFragment<FragmentTopRatedBinding>(FragmentTopRatedBinding::inflate) {

    private val viewModel: TopRatedViewModel by viewModels()
    private val adapter: TopRatedMovieAdapter by lazy {
        TopRatedMovieAdapter()
    }


    override fun start() {
        setUpRV()
        collectMovies()
    }

    private fun setUpRV() {
        with(binding) {
            rvTopRated.adapter = adapter
        }
    }

    private fun collectMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.topRatedMovies.collectLatest {
                    adapter.submitData(it)
                }
            }
        }
    }
}
