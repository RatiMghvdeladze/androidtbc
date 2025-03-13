package com.example.androidtbc.presentation.profile

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentProfileBinding
import com.example.androidtbc.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {
    private val args: ProfileFragmentArgs by navArgs()
    private val profileViewModel: ProfileViewModel by viewModels()
    private var isNavigating = false

    override fun start() {
        setupUI()
        observeViewState()
        observeEvents()

        profileViewModel.processIntent(ProfileIntent.CheckSessionStatus)
        profileViewModel.processIntent(ProfileIntent.LoadUserEmail)
    }

    private fun setupUI() {
        binding.tvYourEmail.text = args.email

        binding.btnLogOut.setOnClickListener {
            profileViewModel.processIntent(ProfileIntent.LogoutUser)
        }
    }

    private fun observeViewState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.viewState.collect { state ->
                    handleViewState(state)
                }
            }
        }
    }

    private fun handleViewState(state: ProfileViewState) {
        binding.btnLogOut.isEnabled = !state.isLoading

        state.userEmail?.let { email ->
            if (email.isNotEmpty() && binding.tvYourEmail.text.isEmpty()) {
                binding.tvYourEmail.text = email
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.events.collect { event ->
                    handleEvent(event)
                }
            }
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