package com.example.androidtbc.presentation.home.innerfragments.nowplaying

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androidtbc.databinding.FragmentNowPlayingBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.home.innerfragments.nowplaying.adapter.NowPlayingAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NowPlayingFragment : BaseFragment<FragmentNowPlayingBinding>(FragmentNowPlayingBinding::inflate) {

    private val viewModel: NowPlayingViewModel by viewModels()
    private val adapter: NowPlayingAdapter by lazy {
        NowPlayingAdapter()
    }


    override fun start() {
        setUpRV()
        collectMovies()
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
}