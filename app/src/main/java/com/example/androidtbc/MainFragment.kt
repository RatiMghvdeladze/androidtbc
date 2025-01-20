package com.example.androidtbc

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtbc.databinding.FragmentMainBinding
import kotlinx.coroutines.launch

class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {
    private val viewModel: MainViewModel by viewModels()
    private val adapter = FieldsAdapter()

    override fun start() {
        setUpRv()
        observeData()
        setUpSearch()
    }

    private fun setUpRv() {
        binding.rvItem.adapter = adapter
        binding.rvItem.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setUpSearch() {
        binding.btnFilter.setOnClickListener {
            val searchQuery = binding.etSearch.text?.toString() ?: ""
            viewModel.search(searchQuery)
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.messages.collect { messages ->
                adapter.submitList(messages)
            }
        }
    }
}
