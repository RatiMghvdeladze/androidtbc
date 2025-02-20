package com.example.androidtbc.presentation.register

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.databinding.FragmentRegisterBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {
    private val viewModel: RegisterViewModel by viewModels()

    override fun start() {
        setUpListeners()
        observeRegisterState()
    }

    private fun observeRegisterState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.registerState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.btnSignUp.isEnabled = false
                    }
                    is Resource.Success -> {
                        binding.btnSignUp.isEnabled = true
                        // Navigate to main screen or home
                        val action = RegisterFragmentDirections.actionRegisterFragmentToSecondRegisterFragment()
                        findNavController().navigate(action)
                        Snackbar.make(binding.root, "Successfully Registered!", Snackbar.LENGTH_SHORT).show()
                        viewModel.resetState()
                    }
                    is Resource.Error -> {
                        binding.btnSignUp.isEnabled = true
                        Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_SHORT).show()
                    }
                    is Resource.Idle -> {
                        binding.btnSignUp.isEnabled = true
                    }
                }
            }
        }
    }

    private fun setUpListeners() {
        with(binding) {
            btnSignUp.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val repeatPassword = etRepeatPassword.text.toString()
                viewModel.signUp(email, password, repeatPassword)
            }

            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            btnSignIn.setOnClickListener {
                findNavController().navigateUp()
            }

            btnGoogle.setOnClickListener {
                viewModel.signUpWithGoogle()
            }
        }
    }
}