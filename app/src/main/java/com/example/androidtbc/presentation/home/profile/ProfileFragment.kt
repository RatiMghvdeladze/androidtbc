package com.example.androidtbc.presentation.home.profile

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.androidtbc.databinding.FragmentProfileBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {
    private val viewModel: ProfileViewModel by viewModels()

    override fun start() {
        loadUserProfile()
        observeUserProfile()
    }

    private fun loadUserProfile() {
        viewModel.getUserProfile()
    }

    private fun observeUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userProfile.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        val user = state.data
                        with(binding) {
                            tvEmail.text = "Email: ${user.email}"
                            tvFullname.text = "Name: ${user.fullName}"
                            tvPhoneNumber.text = "Phone: ${user.phoneNumber}"
                            tvCity.text = "City: ${user.city}"
                        }
                    }
                    is Resource.Error -> {
                        Snackbar.make(binding.root, "Error: ${state.errorMessage}", Snackbar.LENGTH_SHORT).show()
                    }
                    is Resource.Idle -> {
                    }
                }
            }
        }
    }
}