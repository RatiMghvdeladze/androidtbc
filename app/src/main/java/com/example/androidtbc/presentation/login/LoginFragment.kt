package com.example.androidtbc.presentation.login

import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.R
import com.example.androidtbc.data.local.LocalDataStore
import com.example.androidtbc.databinding.FragmentLoginBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.utils.AuthState
import com.example.androidtbc.utils.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private lateinit var loginViewModel: LoginViewModel

    override fun start() {
        initViewModel()
        setupListeners()
        observeAuthState()
        checkSession()
        setupFragmentResultListener()
    }


    private fun initViewModel() {
        loginViewModel = ViewModelProvider(this, ViewModelFactory {
            LoginViewModel(LocalDataStore(requireContext().applicationContext))
        })[LoginViewModel::class.java]
    }

    private fun setupFragmentResultListener() {
        setFragmentResultListener("register_request") { _, bundle ->
            val email = bundle.getString("email")
            val password = bundle.getString("password")

            binding.apply {
                etEmail.setText(email)
                etPassword.setText(password)
            }
        }
    }

    private fun setupListeners() {
        with(binding) {
            btnLogin.setOnClickListener {
                loginViewModel.login(
                    email = etEmail.text.toString(),
                    password = etPassword.text.toString(),
                    rememberMe = cbRememberMe.isChecked
                )
            }

            btnRegister.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
            }
        }
    }

    private fun observeAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.authState.collect { state ->
                    handleAuthState(state)
                }
            }
        }
    }

    private fun handleAuthState(state: AuthState) {
        with(binding) {
            when (state) {
                is AuthState.Loading -> {
                    btnLogin.isEnabled = false
                    btnLogin.text = ""
                    btnLoginProgress.visibility = View.VISIBLE
                }
                is AuthState.Success -> {
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.login)
                    btnLoginProgress.visibility = View.GONE

                    state.message?.let { showSnackbar(it) }

                    state.email?.let { email ->
                        findNavController().navigate(
                            LoginFragmentDirections.actionLoginFragmentToHomeFragment(email)
                        )
                    }
                }
                is AuthState.Error -> {
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.login)
                    btnLoginProgress.visibility = View.GONE
                    showSnackbar(state.message)
                }
                AuthState.Idle -> {
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.login)
                    btnLoginProgress.visibility = View.GONE
                }
            }
        }
    }


    private fun checkSession() {
        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.getEmail().collect { email ->
                if (!email.isNullOrEmpty()) {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToHomeFragment(email)
                    )
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}