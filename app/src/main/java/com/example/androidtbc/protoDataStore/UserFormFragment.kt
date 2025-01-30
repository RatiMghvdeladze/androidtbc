package com.example.androidtbc.protoDataStore

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androidtbc.ViewModelFactory
import com.example.androidtbc.databinding.FragmentUserFormBinding
import com.example.androidtbc.fragments.BaseFragment
import kotlinx.coroutines.launch

class UserFormFragment : BaseFragment<FragmentUserFormBinding>(FragmentUserFormBinding::inflate) {

    private val viewModel: UserFormViewModel by viewModels {
        ViewModelFactory { UserFormViewModel(UserProtoDataStore(requireContext())) }
    }

    override fun start() {
        setupListeners()
        observeData()
    }

    private fun setupListeners() {
        with(binding) {
            btnSave.setOnClickListener {
                val firstName = etFirstName.text.toString()
                val lastName = etLastName.text.toString()
                val email = etEmail.text.toString()

                if (firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank()) {
                    viewModel.saveUserData(firstName, lastName, email)
                    clearInputs()
                }
            }

            btnRead.setOnClickListener {
                viewModel.readUserData()
            }
        }
    }

    private fun clearInputs() {
        with(binding) {
            etFirstName.text?.clear()
            etLastName.text?.clear()
            etEmail.text?.clear()
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userData.collect { preferences ->
                    preferences?.let {
                        binding.tvUserInfo.text = """
                            First Name: ${it.firstName}
                            Last Name: ${it.lastName}
                            Email: ${it.email}
                        """.trimIndent()
                    }
                }
            }
        }
    }
}