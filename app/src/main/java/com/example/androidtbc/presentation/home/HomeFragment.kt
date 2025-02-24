package com.example.androidtbc.presentation.home

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentHomeBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.home.adapter.PopularMoviesAdapter
import com.example.androidtbc.presentation.home.adapter.ViewPagerAdapter
import com.example.androidtbc.presentation.home.innerfragments.nowplaying.NowPlayingFragment
import com.example.androidtbc.presentation.home.innerfragments.popular.PopularFragment
import com.example.androidtbc.presentation.home.innerfragments.toprated.TopRatedFragment
import com.example.androidtbc.presentation.home.innerfragments.upcoming.UpcomingFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var popularMoviesAdapter: PopularMoviesAdapter


    override fun start() {
        setupRecyclerView()
        observeMovies()
        initVP()
    }

    private fun initVP() {
        val fragments = listOf(
            NowPlayingFragment(),
            TopRatedFragment(),
            UpcomingFragment(),
            PopularFragment()
        )

        val tabTitles = listOf(
            getString(R.string.now_playing),
            getString(R.string.top_rated),
            getString(R.string.upcoming),
            getString(R.string.popular)
        )

        with(binding) {
            if (isAdded) {
                vp2.apply {
                    adapter = ViewPagerAdapter(requireActivity(), fragments)
                    offscreenPageLimit = fragments.size
                    reduceDragSensitivity()
                }

                // Connect TabLayout with ViewPager2
                TabLayoutMediator(tabLayout, vp2) { tab, position ->
                    tab.text = tabTitles[position]
                }.attach()

            }
        }
    }

    private fun ViewPager2.reduceDragSensitivity() {
        val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        recyclerViewField.isAccessible = true
        val recyclerView = recyclerViewField.get(this) as RecyclerView

        val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true
        val touchSlop = touchSlopField.get(recyclerView) as Int
        touchSlopField.set(recyclerView, touchSlop * 4) // Multiply by 4 to make it less sensitive
    }

    private fun setupRecyclerView() {
        popularMoviesAdapter = PopularMoviesAdapter()
        binding.rvPopular.apply {
            adapter = popularMoviesAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe loading state
                launch {
                    popularMoviesAdapter.loadStateFlow.collectLatest { loadState ->
                        binding.progressBar.isVisible = loadState.refresh is LoadState.Loading
                    }
                }

                // Observe movies
                launch {
                    viewModel.popularMovies.collectLatest { pagingData ->
                        popularMoviesAdapter.submitData(pagingData)
                    }
                }
            }
        }
    }






}