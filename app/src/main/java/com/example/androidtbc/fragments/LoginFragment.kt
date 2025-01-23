package com.example.androidtbc.fragments

import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.LocalDataStore
import com.example.androidtbc.R
import com.example.androidtbc.ViewModelFactory
import com.example.androidtbc.databinding.FragmentLoginBinding
import com.example.androidtbc.viewModels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private lateinit var loginViewModel: LoginViewModel

    override fun start() {
        initViewModel()
        checkSession()
        setupFragmentResultListener()
        setupListeners()
        observer()
    }

    private fun initViewModel() {
        loginViewModel = ViewModelProvider(this, ViewModelFactory {
            LoginViewModel(LocalDataStore(requireContext().applicationContext))
        })[LoginViewModel::class.java]
    }

    private fun setupFragmentResultListener() {
        parentFragmentManager.setFragmentResultListener("registerResult", viewLifecycleOwner) { _, bundle ->
            val email = bundle.getString("email")
            email?.let { binding.etEmail.setText(it) }
        }
    }

    private fun checkSession() {
        lifecycleScope.launch {
            loginViewModel.getEmail().collect { email ->
                if (!email.isNullOrEmpty()) {
                    navigateToHome(email)
                }
            }
        }
    }

    private fun setupListeners() {
        with(binding){
            btnLogin.isEnabled = false

            etEmail.doOnTextChanged { _, _, _, _ -> validateInputs() }
            etPassword.doOnTextChanged { _, _, _, _ -> validateInputs() }

            btnLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val rememberMeChecked = cbRememberMe.isChecked

                loginViewModel.login(email, password, rememberMeChecked)
            }

            btnRegister.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
            }
        }
    }

    private fun validateInputs() {
        with(binding) {
            btnLogin.isEnabled = loginViewModel.validateEmail(etEmail.text.toString()) &&
                    loginViewModel.validatePassword(etPassword.text.toString())
        }
    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.flowData.collectLatest { response ->
                    if (response != null && response.isSuccessful) {
                        val email = binding.etEmail.text.toString()
                        navigateToHome(email)
                    } else if (response != null) {
                        showSnackbar(getString(R.string.login_failed_please_try_again))
                    }
                }
            }
        }
    }

    private fun navigateToHome(email: String) {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment4(email))
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}