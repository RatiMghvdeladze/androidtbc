package com.example.androidtbc.presentation.home.profile

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentProfileBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.login.LoginViewModel
import com.example.androidtbc.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {
    private val viewModel: ProfileViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    override fun start() {
        loadUserProfile()
        observeUserProfile()
        setUpListeners()
    }

    private fun setUpListeners() {
        with(binding) {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            btnEdit.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment())
            }

            layoutMyAccount.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment())
            }


            layoutLogout.setOnClickListener {
                showLogoutConfirmationDialog()
            }



            layoutHelp.setOnClickListener {
                Snackbar.make(root,
                    getString(R.string.help_support_feature_coming_soon), Snackbar.LENGTH_SHORT).show()
            }

            layoutAbout.setOnClickListener {
                Snackbar.make(root,
                    getString(R.string.about_app_feature_coming_soon), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.are_you_sure_you_want_to_log_out))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()
                logout()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun logout() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                loginViewModel.logout()

                kotlinx.coroutines.delay(300)

                val action = ProfileFragmentDirections.actionProfileFragmentToLoginFragment(fromLogout = true)
                findNavController().navigate(action)
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Logout error: ${e.message}", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadUserProfile() {
        viewModel.getUserProfile()
    }

    private fun observeUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userProfile.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE

                        state.data.let { user ->
                            with(binding) {
                                tvName.text = user.fullName
                                tvEmail.text = user.email
                            }
                        }
                    }
                    is Resource.Error -> {
                        Snackbar.make(binding.root, "Error: ${state.errorMessage}", Snackbar.LENGTH_SHORT).show()
                    }
                    is Resource.Idle -> {
                        // Initial state, do nothing
                    }
                }
            }
        }
    }
}