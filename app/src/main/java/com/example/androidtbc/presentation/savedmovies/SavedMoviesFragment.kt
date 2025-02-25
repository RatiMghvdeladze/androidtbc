package com.example.androidtbc.presentation.savedmovies

import androidx.navigation.fragment.findNavController
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentSavedMoviesBinding
import com.example.androidtbc.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedMoviesFragment : BaseFragment<FragmentSavedMoviesBinding>(FragmentSavedMoviesBinding::inflate) {
    override fun start() {
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavView.apply {
            // Set the saved movies item as selected initially
            selectedItemId = R.id.savedMoviesFragment

            // Set up item selection listener
            setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.homeFragment -> {
                        // Navigate to home fragment
                        findNavController().navigate(R.id.homeFragment)
                        false // Return false to not select this item yet (it will be selected in the HomeFragment)
                    }
                    R.id.savedMoviesFragment -> {
                        // We're already on the saved movies fragment, so just return true
                        true
                    }
                    else -> false
                }
            }
        }
    }
}