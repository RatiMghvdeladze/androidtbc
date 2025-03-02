package com.example.androidtbc.presentation.savedmovies

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentSavedMoviesBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.model.MovieUI
import com.example.androidtbc.presentation.savedmovies.adapter.SavedMovieAdapter
import com.example.androidtbc.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedMoviesFragment : BaseFragment<FragmentSavedMoviesBinding>(FragmentSavedMoviesBinding::inflate) {
    private val viewModel: SavedMoviesViewModel by viewModels()
    private lateinit var adapter: SavedMovieAdapter

    override fun start() {
        setUpListeners()
        setUpRV()
        setupBottomNavigation()
        observeSavedMovies()
        fetchSavedMovies()
    }

    private fun setUpListeners() {
        with(binding) {
            btnClearAll.setOnClickListener {
                showClearAllConfirmationDialog()
            }
            btnBack.setOnClickListener{
                findNavController().navigateUp()
            }
        }
    }

    private fun showClearAllConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.clear_watchlist))
            .setMessage(getString(R.string.are_you_sure_you_want_to_clear_all_saved_movies))
            .setPositiveButton(getString(R.string.clear_all)) { _, _ ->
                viewModel.clearAllSavedMovies()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        fetchSavedMovies()
    }

    private fun fetchSavedMovies() {
        viewModel.fetchSavedMovies()
    }

    private fun setUpRV() {
        adapter = SavedMovieAdapter(
            onMovieClicked = { movie ->
                findNavController().navigate(
                    SavedMoviesFragmentDirections.actionSavedMoviesFragmentToMovieDetailFragment(movie.id)
                )
            },
            onMovieLongClicked = { movie ->
                showDeleteConfirmationDialog(movie)
                true
            }
        )

        binding.rvSavedMovies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SavedMoviesFragment.adapter
        }
    }

    private fun showDeleteConfirmationDialog(movie: MovieUI) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.remove_from_watchlist))
            .setMessage(getString(R.string.are_you_sure_you_want_to_remove_this_movie_from_your_watchlist))
            .setPositiveButton(getString(R.string.remove)) { _, _ ->
                viewModel.deleteMovie(movie.id)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun observeSavedMovies() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.savedMovies.collect { result ->
                    when (result) {
                        is Resource.Loading -> showLoading()
                        is Resource.Success -> {
                            hideLoading()
                            adapter.submitList(result.data)
                            updateEmptyState(result.data.isEmpty())
                        }
                        is Resource.Error -> {
                            hideLoading()
                            Toast.makeText(requireContext(), result.errorMessage, Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvSavedMovies.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.rvSavedMovies.visibility = View.VISIBLE
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.rvSavedMovies.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
        } else {
            binding.rvSavedMovies.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
        }
    }

    private fun setupBottomNavigation() {
        with(binding.bottomNavView) {
            selectedItemId = R.id.savedMoviesFragment

            setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.homeFragment -> {
                        if (selectedItemId != R.id.homeFragment) {
                            findNavController().navigate(
                                R.id.homeFragment,
                                null,
                                navOptions {
                                    popUpTo(R.id.my_nav) {
                                        inclusive = false
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            )
                        }
                        true
                    }
                    R.id.savedMoviesFragment -> {
                        if (selectedItemId != R.id.savedMoviesFragment) {
                            findNavController().navigate(
                                R.id.savedMoviesFragment,
                                null,
                                navOptions {
                                    popUpTo(R.id.my_nav) {
                                        inclusive = false
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            )
                        }
                        true
                    }
                    else -> false
                }
            }
        }
    }
}