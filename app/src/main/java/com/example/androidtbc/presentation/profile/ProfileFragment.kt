package com.example.androidtbc.presentation.profile

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.androidtbc.databinding.FragmentProfileBinding
import com.example.androidtbc.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {
    private val args: ProfileFragmentArgs by navArgs()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun start() {
        displayEmail()
        setupLogoutButton()
        observeEmail()
    }

    private fun setupLogoutButton() {
        binding.btnLogOut.setOnClickListener {
            profileViewModel.clearUserData()
        }
    }

    private fun observeEmail() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.getEmail().collect { email ->
                    if (email.isNullOrEmpty()) {
                        findNavController().navigate(
                            ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
                        )
                    }
                }
            }
        }
    }

    private fun displayEmail() {
        val email = args.email
        binding.tvYourEmail.text = email
    }
}