package com.example.androidtbc.presentation.category

import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtbc.databinding.FragmentCategoryBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.category.adapter.CategoryAdapter
import com.example.androidtbc.presentation.extension.launchLatest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment : BaseFragment<FragmentCategoryBinding>(FragmentCategoryBinding::inflate) {
    private val viewModel: CategoryViewModel by viewModels()
    private val categoryAdapter by lazy {
        CategoryAdapter()
    }

    override fun start() {
        setUpRv()
        setUpListeners()
        observeState()
    }

    private fun setUpRv() {
        with(binding.rvCategories) {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setUpListeners() {
        with(binding) {
            etSearch.addTextChangedListener {
                viewModel.onEvent(CategoryEvent.OnSearchQueryChanged(it.toString()))
            }
        }
    }

    private fun observeState() {
        launchLatest(viewModel.state) { state ->
            categoryAdapter.submitList(state.categories)
            binding.progressBar.isVisible = state.isLoading
        }
    }
}