package com.example.androidtbc.presentation.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentRegisterBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.utils.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun start() {
        setupListeners()
        observeRegistrationState()
    }

    private fun setupListeners() {
        with(binding) {
            btnRegister.setOnClickListener {
                registerViewModel.register(
                    email = etEmail.text.toString(),
                    password = etPassword.text.toString(),
                    repeatPassword = etRepeatPassword.text.toString()
                )
            }

            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun observeRegistrationState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.registrationState.collect { state ->
                    handleRegistrationState(state)
                }
            }
        }
    }

    private fun handleRegistrationState(state: Resource<String>) {
        with(binding) {
            when (state) {
                is Resource.Idle -> {
                    btnRegister.isEnabled = true
                    btnRegister.text = getString(R.string.register)
                    btnRegisterProgress.visibility = View.GONE
                }
                is Resource.Loading -> {
                    btnRegister.isEnabled = false
                    btnRegister.text = ""
                    btnRegisterProgress.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    btnRegister.isEnabled = true
                    btnRegister.text = getString(R.string.register)
                    btnRegisterProgress.visibility = View.GONE

                    showSnackbar("Registration successful!")

                    setFragmentResult("register_request", Bundle().apply {
                        putString("email", state.data)
                        putString("password", binding.etPassword.text.toString())
                    })
                    findNavController().popBackStack()
                }
                is Resource.Error -> {
                    btnRegister.isEnabled = true
                    btnRegister.text = getString(R.string.register)
                    btnRegisterProgress.visibility = View.GONE
                    showSnackbar(state.errorMessage)
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}