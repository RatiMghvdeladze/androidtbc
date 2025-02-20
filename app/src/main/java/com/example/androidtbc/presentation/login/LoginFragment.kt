package com.example.androidtbc.presentation.login

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentLoginBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun start() {
        setUpListeners()
        observeLoginState()
    }

    private fun observeLoginState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loginState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            binding.btnSignIn.isEnabled = false
                            binding.progressBarSignIn.visibility = View.VISIBLE
                            binding.btnSignIn.text = ""
                        }

                        is Resource.Success -> {
                            binding.btnSignIn.isEnabled = true
                            binding.progressBarSignIn.visibility = View.GONE
                            binding.btnSignIn.text = getString(R.string.sign_in)
                            // Navigate to main screen or home
                            Snackbar.make(binding.root, "Successfully logged in", Snackbar.LENGTH_SHORT).show()
                            loginViewModel.resetState()
                        }

                        is Resource.Error -> {
                            binding.btnSignIn.isEnabled = true
                            binding.progressBarSignIn.visibility = View.GONE
                            binding.btnSignIn.text = getString(R.string.sign_in)
                            Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_SHORT).show()
                        }

                        is Resource.Idle -> {
                            binding.btnSignIn.isEnabled = true
                            binding.progressBarSignIn.visibility = View.GONE
                            binding.btnSignIn.text = getString(R.string.sign_in)
                        }
                    }
                }
            }
        }
    }

    private fun setUpListeners() {
        with(binding) {
            btnSignIn.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                loginViewModel.signIn(email, password)
            }

            btnSignUp.setOnClickListener {
                val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                findNavController().navigate(action)
            }

            btnGoogle.setOnClickListener {
                loginViewModel.signInWithGoogle()
            }
        }
    }
}