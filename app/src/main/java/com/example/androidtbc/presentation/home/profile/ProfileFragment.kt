package com.example.androidtbc.presentation.home.profile

import android.content.Context
import android.content.res.Configuration
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
import java.util.Locale

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
            layoutLanguage.setOnClickListener {
                showLanguageSelectionDialog()
            }
        }
    }
    private fun showLanguageSelectionDialog() {
        val languages = arrayOf("English", "Français", "Español", "Deutsch", "Georgian")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.select_language))
            .setItems(languages) { dialog, which ->
                val locale = when (which) {
                    0 -> Locale("en")
                    1 -> Locale("fr")
                    2 -> Locale("es")
                    3 -> Locale("de")
                    4 -> Locale("ka")
                    else -> Locale("en")
                }
                updateLocale(locale)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
    private fun updateLocale(locale: Locale) {
        // Save the selected language preference
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("app_language", locale.language).apply()

        // Create updated configuration
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)

        // Create context with the new configuration
        val context = requireContext().createConfigurationContext(configuration)

        // Update resources with the new configuration
        val resources = context.resources

        // Restart the activity to apply changes
        requireActivity().recreate()
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