package com.example.androidtbc.presentation.moviedetail.tablayoutfragments.cast

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidtbc.databinding.FragmentCastBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.moviedetail.tablayoutfragments.cast.adapter.CastAdapter
import com.example.androidtbc.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CastFragment : BaseFragment<FragmentCastBinding>(FragmentCastBinding::inflate) {
    private val viewModel: CastViewModel by viewModels()
    private val args: CastFragmentArgs by navArgs()
    private lateinit var castAdapter: CastAdapter

    override fun start() {
        val movieId = args.movieId
        setupRecyclerView()
        viewModel.getMovieCast(movieId)
        setupObservers()
    }

    private fun setupRecyclerView() {
        castAdapter = CastAdapter { castMember ->
            // Show bottom sheet when cast is clicked
            CastBottomSheetFragment(castMember).show(childFragmentManager, "CastBottomSheet")
        }
        binding.rvCasts.apply {
            adapter = castAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.castDetails.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            showLoading(true)
                        }

                        is Resource.Success -> {
                            showLoading(false)
                            resource.data.cast.let { castList ->
                                castAdapter.submitList(castList)
                            }
                        }

                        is Resource.Error -> {
                            showLoading(false)
                            showError(resource.errorMessage)
                        }

                        else -> {
                            showLoading(false)
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvCasts.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).setAction("Retry") {
            viewModel.getMovieCast(args.movieId)
        }.show()
    }
}