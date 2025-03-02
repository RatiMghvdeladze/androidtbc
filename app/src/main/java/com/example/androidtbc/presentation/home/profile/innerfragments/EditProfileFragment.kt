package com.example.androidtbc.presentation.home.profile.innerfragments

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.R
import com.example.androidtbc.data.remote.dto.User
import com.example.androidtbc.databinding.FragmentEditProfileBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>(FragmentEditProfileBinding::inflate) {
    private val viewModel: EditProfileViewModel by viewModels()
    private var originalUser: User? = null

    override fun start() {
        observeUserProfile()
        setUpListeners()
    }

    private fun setUpListeners() {
        with(binding) {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            btnUpdateProfile.setOnClickListener {
                if (validateInputs()) {
                    updateUserProfile()
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        with(binding) {
            if (etName.text.toString().trim().isEmpty()) {
                tilName.error = getString(R.string.error_field_required)
                isValid = false
            } else {
                tilName.error = null
            }

            if (etPhone.text.toString().trim().isEmpty()) {
                tilPhone.error = getString(R.string.error_field_required)
                isValid = false
            } else {
                tilPhone.error = null
            }

            if (etCity.text.toString().trim().isEmpty()) {
                tilCity.error = getString(R.string.error_field_required)
                isValid = false
            } else {
                tilCity.error = null
            }
        }

        return isValid
    }

    private fun hasChanges(): Boolean {
        originalUser?.let { user ->
            val name = binding.etName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val city = binding.etCity.text.toString().trim()

            return name != user.fullName || phone != user.phoneNumber || city != user.city
        }

        return true
    }

    private fun updateUserProfile() {
        if (!hasChanges()) {
            Snackbar.make(binding.root, getString(R.string.no_changes_made), Snackbar.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val city = binding.etCity.text.toString().trim()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                showProgress(true)

                val result = viewModel.saveUserInfo(name, phone, city)

                showProgress(false)

                if (result.isSuccess) {
                    Snackbar.make(binding.root, getString(R.string.profile_updated_successfully), Snackbar.LENGTH_SHORT).show()

                    findNavController().navigateUp()
                } else {
                    Snackbar.make(binding.root, result.exceptionOrNull()?.message ?: getString(R.string.error_updating_profile), Snackbar.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                showProgress(false)
                Snackbar.make(binding.root, e.message ?: getString(R.string.error_updating_profile), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userProfile.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        showProgress(true)
                    }
                    is Resource.Success -> {
                        showProgress(false)
                        originalUser = state.data
                        setFields(state.data)
                    }
                    is Resource.Error -> {
                        showProgress(false)
                        Snackbar.make(binding.root, "Error: ${state.errorMessage}", Snackbar.LENGTH_SHORT).show()
                    }
                    is Resource.Idle -> {
                        viewModel.getUserProfile()
                    }
                }
            }
        }
    }

    private fun setFields(user: User) {
        with(binding) {
            etName.setText(user.fullName)
            etPhone.setText(user.phoneNumber)
            etCity.setText(user.city)
        }
    }

    private fun showProgress(show: Boolean) {
        with(binding) {
            progressBar.visibility = if (show) View.VISIBLE else View.GONE

            btnUpdateProfile.isEnabled = !show
            etName.isEnabled = !show
            etPhone.isEnabled = !show
            etCity.isEnabled = !show
        }
    }
}