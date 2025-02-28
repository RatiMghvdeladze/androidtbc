package com.example.androidtbc.presentation.register

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentRegisterBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun start() {
        setUpListeners()
        observeRegisterState()
    }

    private fun observeRegisterState() {
        viewLifecycleOwner.lifecycleScope.launch {
            registerViewModel.registerState.collect { state ->
                with(binding) {
                    when (state) {
                        is Resource.Loading -> {
                            btnSignUp.isEnabled = false
                            progressBarSignUp.visibility = View.VISIBLE
                            btnSignUp.text = ""
                        }
                        is Resource.Success -> {
                            btnSignUp.isEnabled = true
                            progressBarSignUp.visibility = View.GONE
                            btnSignUp.text = getString(R.string.sign_up)

                            val email = etEmail.text.toString()
                            val password = etPassword.text.toString()

                            registerViewModel.setUserCredentials(email, password)

                            val action = RegisterFragmentDirections.actionRegisterFragmentToSecondRegisterFragment(
                                email = email,
                                password = password
                            )
                            findNavController().navigate(action)

                            registerViewModel.resetState()
                        }
                        is Resource.Error -> {
                            btnSignUp.isEnabled = true
                            progressBarSignUp.visibility = View.GONE
                            btnSignUp.text = getString(R.string.sign_up)
                            Snackbar.make(root, state.errorMessage, Snackbar.LENGTH_SHORT).show()
                        }
                        is Resource.Idle -> {
                            btnSignUp.isEnabled = true
                            progressBarSignUp.visibility = View.GONE
                            btnSignUp.text = getString(R.string.sign_up)
                        }
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

                registerViewModel.signUp(email, password, repeatPassword)
            }

            btnSignIn.setOnClickListener {
                findNavController().navigateUp()
            }

            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}