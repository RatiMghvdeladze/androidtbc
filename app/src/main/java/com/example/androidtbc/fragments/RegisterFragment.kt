package com.example.androidtbc.fragments

import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentRegisterBinding
import com.example.androidtbc.viewModels.RegisterViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun start() {
        setupListeners()
        observer()
        observeErrors()
    }

    private fun observeErrors() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.error.collect { errorMessage ->
                    errorMessage?.let {
                        showSnackbar(it)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        with(binding) {
            btnRegister.setOnClickListener {
                etEmail.doOnTextChanged { _, _, _, _ -> validateInputs() }
                etPassword.doOnTextChanged { _, _, _, _ -> validateInputs() }
                etRepeatPassword.doOnTextChanged { _, _, _, _ -> validateInputs() }

                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val repeatPassword = etRepeatPassword.text.toString()

                if (registerViewModel.validateEmail(email) &&
                    registerViewModel.validatePassword(password) &&
                    password == repeatPassword) {
                    registerViewModel.register(email, password)
                } else {
                    if (!registerViewModel.validateEmail(email)) {
                        showSnackbar(getString(R.string.invalid_email))
                    } else if (!registerViewModel.validatePassword(password)) {
                        showSnackbar(getString(R.string.invalid_password))
                    } else if (password != repeatPassword) {
                        showSnackbar(getString(R.string.the_passwords_don_t_match))
                    } else {
                        showSnackbar(getString(R.string.please_fill_all_fields))
                    }
                }
            }

            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun validateInputs() {
        with(binding) {
            btnRegister.isEnabled = registerViewModel.validateEmail(etEmail.text.toString()) &&
                    registerViewModel.validatePassword(etPassword.text.toString()) &&
                    etPassword.text.toString() == etRepeatPassword.text.toString()
        }
    }
    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.flowData.collect { response ->
                    if (response != null && response.isSuccessful) {
                        val email = binding.etEmail.text.toString()
                        val password = binding.etPassword.text.toString()

                        setFragmentResult("register_request", Bundle().apply {
                            putString("email", email)
                            putString("password", password)
                        })

                        findNavController().popBackStack()
                    } else if (response != null) {
                        showSnackbar(getString(R.string.registration_failed_please_try_again))
                    }
                }
            }
        }
    }



    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
