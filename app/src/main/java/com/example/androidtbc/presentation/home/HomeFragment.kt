package com.example.androidtbc.presentation.home

import android.text.Editable
import android.text.TextWatcher
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
import com.example.androidtbc.presentation.home.adapter.PopularMoviesSearchAdapter
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
    private lateinit var searchResultsAdapter: PopularMoviesSearchAdapter
    private var isSearchActive = false

    override fun start() {
        setupRecyclerView()
        setupSearchListener()
        observeMovies()
        initVP()
    }

    private fun setupSearchListener() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                isSearchActive = query.isNotEmpty()

                if (isSearchActive) {
                    viewModel.setSearchQuery(query)
                } else {
                    viewModel.setSearchQuery("")
                }

                updateUIForSearchState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateUIForSearchState() {
        with(binding) {
            if (isSearchActive) {
                vp2.isVisible = false
                tabLayout.isVisible = false
                rvPopular.isVisible = false

                rvSearchResults.isVisible = true
            } else {
                vp2.isVisible = true
                tabLayout.isVisible = true
                rvPopular.isVisible = true

                rvSearchResults.isVisible = false
            }
        }
    }

    private fun observeMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.popularMovies.collectLatest { pagingData ->
                        popularMoviesAdapter.submitData(pagingData)
                    }
                }

                launch {
                    viewModel.searchResults.collectLatest { pagingData ->
                        searchResultsAdapter.submitData(pagingData)
                    }
                }

                launch {
                    popularMoviesAdapter.loadStateFlow.collectLatest { loadState ->
                        binding.progressBar.isVisible = loadState.refresh is LoadState.Loading && !isSearchActive
                    }
                }

                launch {
                    searchResultsAdapter.loadStateFlow.collectLatest { loadState ->
                        binding.progressBar.isVisible = loadState.refresh is LoadState.Loading && isSearchActive
                    }
                }
            }
        }
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
        touchSlopField.set(recyclerView, touchSlop * 4)
    }

    private fun setupRecyclerView() {
        popularMoviesAdapter = PopularMoviesAdapter()
        binding.rvPopular.apply {
            adapter = popularMoviesAdapter
            setHasFixedSize(true)
        }

        searchResultsAdapter = PopularMoviesSearchAdapter()
        binding.rvSearchResults.apply {
            adapter = searchResultsAdapter
            setHasFixedSize(true)
        }
    }






}