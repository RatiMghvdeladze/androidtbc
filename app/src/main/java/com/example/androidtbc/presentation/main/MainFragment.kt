package com.example.androidtbc.presentation.main

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.androidtbc.ItemDTO
import com.example.androidtbc.TravelAdapter
import com.example.androidtbc.databinding.FragmentMainBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var travelAdapter: TravelAdapter

    override fun start() {
        setUpViewPager()
        observers()
    }

    private fun setUpViewPager() {
        binding.viewPager.adapter = travelAdapter

        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.14f
        }

        binding.viewPager.apply {
            setPageTransformer(transformer)
            offscreenPageLimit = 3
            clipToPadding = false
            clipChildren = false
            setPadding(80, 0, 80, 0)
        }
    }

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect { resource ->
                    handleResource(resource)
                }
            }
        }
    }

    private fun handleResource(resource: Resource<List<ItemDTO>>) {
        binding.progressBar.visibility = if (resource is Resource.Loading) View.VISIBLE else View.GONE

        when (resource) {
            Resource.Idle -> Unit
            Resource.Loading -> Unit
            is Resource.Success -> {
                travelAdapter.submitList(resource.data)
            }
            is Resource.Error -> {
                Snackbar.make(binding.root, resource.errorMessage, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
