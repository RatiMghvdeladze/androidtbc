package com.example.androidtbc.presentation.moviedetail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.androidtbc.R
import com.example.androidtbc.data.remote.dto.MovieDetailDto
import com.example.androidtbc.databinding.FragmentMovieDetailBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.home.adapter.ViewPagerAdapter
import com.example.androidtbc.presentation.moviedetail.innerfragments.aboutmovie.AboutMovieFragment
import com.example.androidtbc.presentation.moviedetail.innerfragments.cast.CastFragment
import com.example.androidtbc.utils.Resource
import com.example.androidtbc.utils.loadTmdbImage
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class MovieDetailFragment : BaseFragment<FragmentMovieDetailBinding>(FragmentMovieDetailBinding::inflate) {

    private val viewModel: MovieDetailViewModel by viewModels()
    private val args: MovieDetailFragmentArgs by navArgs()

    override fun start() {
        setupListeners()
        initVP()
        fetchMovieDetails()
        observeMovieDetails()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Add click listener for rating container
        binding.ratingContainer.setOnClickListener {
            // Toggle vote count visibility
            val isVisible = binding.tvVoteCount.visibility == View.VISIBLE
            binding.tvVoteCount.visibility = if (isVisible) View.GONE else View.VISIBLE
        }
    }

    private fun fetchMovieDetails() {
        viewModel.getMovieDetails(args.movieId)
    }

    private fun observeMovieDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieDetails.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading()
                        is Resource.Success -> displayMovieDetails(resource.data)
                        is Resource.Error -> showError(resource.errorMessage)
                        else -> {}
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun displayMovieDetails(movie: MovieDetailDto) {
        binding.progressBar.visibility = View.GONE // Hide when data loads

        with(binding) {
            tvMovieTitle.text = movie.title

            movie.backdropPath?.let { path ->
                ivBanner.loadTmdbImage(path)
            }

            movie.posterPath?.let { path ->
                ivPoster.loadTmdbImage(path)
            }

            if(movie.adult) ivAgeRestriction.visibility = View.VISIBLE

            // Format the rating to display only one decimal place
            tvRating.text = String.format("%.1f", movie.voteAverage)

            tvVoteCount.text = "(${NumberFormat.getNumberInstance(Locale.US).format(movie.voteCount)})"

            val year = movie.releaseDate.split("-").firstOrNull() ?: ""
            tvReleaseYear.text = year
            movie.runtime?.let { minutes ->
                tvDuration.text = getString(R.string.minutes_format, minutes)
            }
            movie.genres?.firstOrNull()?.let { genre ->
                tvGenre.text = genre.name
            }
        }
    }


    private fun initVP() {
        val movieId = args.movieId  // Ensure movieId is retrieved properly

        val fragments = listOf(
            AboutMovieFragment().apply {
                arguments = Bundle().apply {
                    putInt("movieId", movieId)  // Pass movieId as an argument
                }
            },
            CastFragment().apply {
                arguments = Bundle().apply {
                    putInt("movieId", movieId)
                }
            }
        )


        val tabTitles = listOf(
            getString(R.string.about_movie),
            getString(R.string.cast)
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
}