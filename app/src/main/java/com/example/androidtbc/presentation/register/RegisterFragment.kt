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
import com.example.androidtbc.utils.AuthState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun start() {
        setupListeners()
        observeAuthState()
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

    private fun observeAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.authState.collect { state ->
                    handleAuthState(state)
                }
            }
        }
    }

    private fun handleAuthState(state: AuthState) {
        with(binding) {
            when (state) {
                is AuthState.Loading -> {
                    btnRegister.isEnabled = false
                    btnRegister.text = ""
                    btnRegisterProgress.visibility = View.VISIBLE
                }
                is AuthState.Success -> {
                    btnRegister.isEnabled = true
                    btnRegister.text = getString(R.string.register)
                    btnRegisterProgress.visibility = View.GONE

                    state.message?.let { showSnackbar(it) }

                    state.email?.let { email ->
                        setFragmentResult("register_request", Bundle().apply {
                            putString("email", email)
                            putString("password", binding.etPassword.text.toString())
                        })
                        findNavController().popBackStack()
                    }
                }
                is AuthState.Error -> {
                    btnRegister.isEnabled = true
                    btnRegister.text = getString(R.string.register)
                    btnRegisterProgress.visibility = View.GONE
                    showSnackbar(state.message)
                }
                AuthState.Idle -> {
                    btnRegister.isEnabled = true
                    btnRegister.text = getString(R.string.register)
                    btnRegisterProgress.visibility = View.GONE
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}