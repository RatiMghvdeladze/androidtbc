package com.example.androidtbc.presentation.login

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentLoginBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val loginViewModel: LoginViewModel by viewModels()
    private val args: LoginFragmentArgs by navArgs()

    override fun start() {
        fillFields()
        setUpListeners()
        observeLoginState()
        checkIfAlreadyLoggedIn()
    }

    private fun fillFields() {
        binding.etEmail.setText(args.email)
        binding.etPassword.setText(args.password)
    }

    private fun checkIfAlreadyLoggedIn() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.isUserLoggedIn.collectLatest { isLoggedIn ->
                    // Only navigate if we came here directly, not from logout
                    if (isLoggedIn && !args.fromLogout) {
                        safeNavigateToHome()
                    }
                }
            }
        }
    }

    private fun safeNavigateToHome() {
        val navController = findNavController()
        if (navController.currentDestination?.id == R.id.loginFragment) {
            val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
            navController.navigate(action)
        }
    }

    private fun observeLoginState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loginState.collectLatest { state ->
                    with(binding) {
                        when (state) {
                            is Resource.Loading -> {
                                btnSignIn.isEnabled = false
                                progressBarSignIn.visibility = View.VISIBLE
                                btnSignIn.text = ""
                            }

                            is Resource.Success -> {
                                btnSignIn.isEnabled = true
                                progressBarSignIn.visibility = View.GONE
                                btnSignIn.text = getString(R.string.sign_in)
                                safeNavigateToHome()
                                loginViewModel.resetState()
                            }

                            is Resource.Error -> {
                                btnSignIn.isEnabled = true
                                progressBarSignIn.visibility = View.GONE
                                btnSignIn.text = getString(R.string.sign_in)
                                Snackbar.make(
                                    binding.root,
                                    state.errorMessage,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }

                            is Resource.Idle -> {
                                btnSignIn.isEnabled = true
                                progressBarSignIn.visibility = View.GONE
                                btnSignIn.text = getString(R.string.sign_in)
                            }
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

                // Add validation here
                if (email.isEmpty() || password.isEmpty()) {
                    val errorMessage = when {
                        email.isEmpty() && password.isEmpty() -> "Email and password must be filled"
                        email.isEmpty() -> "Email must be filled"
                        else -> "Password must be filled"
                    }
                    Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                loginViewModel.signIn(email, password, cbRememberMe.isChecked)
            }

            btnSignUp.setOnClickListener {
                val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                findNavController().navigate(action)
            }
        }
    }
}