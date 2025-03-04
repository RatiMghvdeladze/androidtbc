package com.example.androidtbc.presentation.home.profile

import android.content.Context
import android.content.res.Configuration
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentProfileBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.home.profile.adapter.ProfileItemAdapter
import com.example.androidtbc.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var profileAdapter: ProfileItemAdapter

    override fun start() {
        setUpRv()
        loadUserProfile()
        observeUserProfile()
        setupProfileHeader()
    }

    private fun setUpRv() {
        profileAdapter = ProfileItemAdapter { menuItem ->
            handleMenuItemClick(menuItem)
        }

        with(binding.rvProfileMenu) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = profileAdapter

        }

        profileAdapter.submitList(createProfileMenuItems())
    }

    private fun createProfileMenuItems(): List<ProfileMenuItem> {
        return listOf(
            ProfileMenuItem(
                id = 1,
                iconResId = R.drawable.ic_person_for_profile_page,
                backgroundResId = R.drawable.bg_profile_page_item,
                titleResId = R.string.my_account,
                descriptionResId = R.string.make_changes_to_your_account,
                showDivider = true
            ),

            ProfileMenuItem(
                id = 2,
                iconResId = R.drawable.ic_language,
                backgroundResId = R.drawable.bg_profile_page_item,
                iconTint = R.color.indigo_500,
                titleResId = R.string.change_language,
                showDivider = true
            ),

            ProfileMenuItem(
                id = 3,
                iconResId = R.drawable.ic_help_support,
                backgroundResId = R.drawable.bg_profile_page_item,
                iconTint = R.color.indigo_500,
                titleResId = R.string.help_amp_support,
                showDivider = true
            ),

            ProfileMenuItem(
                id = 4,
                iconResId = R.drawable.ic_heart_about_app,
                backgroundResId = R.drawable.bg_profile_page_item,
                iconTint = R.color.indigo_500,
                titleResId = R.string.about_app,
                showDivider = true
            ),

            ProfileMenuItem(
                id = 5,
                iconResId = R.drawable.ic_logout,
                backgroundResId = R.drawable.bg_profile_page_item,
                titleResId = R.string.log_out,
                descriptionResId = R.string.further_secure_your_account_for_safety,
                showDivider = false
            )
        )
    }

    private fun handleMenuItemClick(menuItem: ProfileMenuItem) {
        when (menuItem.id) {
            1 -> findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment())
            2 -> showLanguageSelectionDialog()
            3 -> Snackbar.make(
                binding.root,
                getString(R.string.help_support_feature_coming_soon),
                Snackbar.LENGTH_SHORT
            ).show()

            4 -> Snackbar.make(
                binding.root,
                getString(R.string.about_app_feature_coming_soon),
                Snackbar.LENGTH_SHORT
            ).show()

            5 -> showLogoutConfirmationDialog()
        }
    }

    private fun setupProfileHeader() {
        with(binding) {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            btnEdit.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment())
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
        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("app_language", locale.language).apply()

        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)

        val context = requireContext().createConfigurationContext(configuration)

        val resources = context.resources

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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.logout()

                delay(300)

                val action =
                    ProfileFragmentDirections.actionProfileFragmentToLoginFragment(fromLogout = true)
                findNavController().navigate(action)
            }
        }
    }

    private fun loadUserProfile() {
        viewModel.getUserProfile()
    }

    private fun observeUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
                            binding.progressBar.visibility = View.GONE
                            Snackbar.make(
                                binding.root,
                                "Error: ${state.errorMessage}",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }

                        is Resource.Idle -> {}
                    }
                }
            }
        }
    }
}