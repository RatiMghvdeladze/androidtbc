package com.example.androidtbc.presentation.moviedetail.tablayoutfragments.aboutmovie

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentAboutMovieBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class AboutMovieFragment : BaseFragment<FragmentAboutMovieBinding>(FragmentAboutMovieBinding::inflate) {

    private val viewModel: AboutMovieViewModel by viewModels()
    private val args: AboutMovieFragmentArgs by navArgs()

    override fun start() {
        val movieId = args.movieId
        viewModel.getMovieDetails(movieId)

        observeMovieDetails()
    }

    private fun observeMovieDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieDetail.collectLatest { resource ->
                    with(binding) {
                        when (resource) {
                            is Resource.Loading -> {
                                binding.tvOverview.text = getString(R.string.loading)
                            }

                            is Resource.Success -> {
                                resource.data.let { movie ->
                                    tvOverview.text = movie.overview
                                    tvStatus.text = movie.status
                                    tvLanguage.text = movie.originalLanguage

                                    val formattedBudget = NumberFormat.getNumberInstance(Locale.US)
                                        .format(movie.budget)
                                    val formattedRevenue = NumberFormat.getNumberInstance(Locale.US)
                                        .format(movie.revenue)

                                    tvBudget.text = "$$formattedBudget"
                                    tvRevenue.text = "$$formattedRevenue"

                                }
                            }

                            is Resource.Error -> {
                                showErrorMessage(resource.errorMessage)
                            }

                            is Resource.Idle -> {}
                        }

                    }
                }
            }
        }
    }

    private fun showErrorMessage(message: String) {
        binding.tvOverview.text = ""
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

}
