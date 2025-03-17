package com.example.androidtbc.presentation.profile

import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentProfileBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.extension.launchLatest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {
    private val profileViewModel: ProfileViewModel by viewModels()
    private var isNavigating = false

    override fun start() {
        setupUI()
        observeViewState()
        observeEvents()
    }

    private fun setupUI() {
        binding.btnLogOut.setOnClickListener {
            profileViewModel.onEvent(ProfileEvent.LogoutUser)
        }
    }

    private fun observeViewState() {
        launchLatest(profileViewModel.state) { state ->
            handleViewState(state)
        }
    }

    private fun handleViewState(state: ProfileState) {
        binding.progressBar.isVisible = state.isLoading
        binding.btnLogOut.isEnabled = !state.isLoading

        state.userEmail?.let { email ->
            if (email.isNotEmpty() && binding.tvYourEmail.text.isEmpty()) {
                binding.tvYourEmail.text = email
            }
        }
    }

    private fun observeEvents() {
        launchLatest(profileViewModel.events) { event ->
            handleEvent(event)
        }
    }

    private fun handleEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.NavigateToLogin -> {
                if (!isNavigating) {
                    isNavigating = true
                    navigateToLogin()
                }
            }
            is ProfileEvent.ShowSnackbar -> {
                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
            }
            else -> {
            }
        }
    }

    private fun navigateToLogin() {
        try {
            val currentDestinationId = findNavController().currentDestination?.id
            if (currentDestinationId == R.id.profileFragment) {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
                )
            } else if (currentDestinationId != R.id.loginFragment) {
                findNavController().navigate(R.id.loginFragment)
            }
        } catch (e: Exception) {
            findNavController().popBackStack(R.id.loginFragment, false)
        }
    }
}