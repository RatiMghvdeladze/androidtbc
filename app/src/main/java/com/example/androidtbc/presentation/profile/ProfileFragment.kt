// Update the ProfileFragment.kt
package com.example.androidtbc.presentation.profile

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
        displayEmail()
        setUpBtnLogOut()
        observerLogout()
        checkSessionStatus()
    }

    private fun setUpBtnLogOut() {
        binding.btnLogOut.setOnClickListener {
            profileViewModel.logoutCompletely()
        }
    }

    private fun observerLogout() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.isLoggingOut.collect {
                    if (it && !isNavigating) {
                        isNavigating = true
                        navigateToLogin()
                    }
                }
            }
        }
    }

    private fun checkSessionStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.isSessionActive().collect { existToken ->
                    if (!existToken && !isNavigating) {
                        isNavigating = true
                        navigateToLogin()
                    }
                }
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

    private fun displayEmail() {
        val email = args.email
        binding.tvYourEmail.text = email
    }
}