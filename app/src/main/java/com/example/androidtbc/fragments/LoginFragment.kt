package com.example.androidtbc.fragments

import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.R
import com.example.androidtbc.SessionManager
import com.example.androidtbc.databinding.FragmentLoginBinding
import com.example.androidtbc.viewModels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun start() {
        sessionManager = SessionManager(requireContext())
        checkSession()
        setupListeners()
        observer()
        getResultFromRegister()
    }

    private fun checkSession() {
        val email = sessionManager.getEmail()
        val isRememberMeEnabled = sessionManager.isRememberMeEnabled()

        if (!email.isNullOrEmpty() && isRememberMeEnabled) {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment4(email))
        }
    }

    private fun setupListeners() {
        binding.apply {
            btnLogin.isEnabled = false

            etEmail.doOnTextChanged { _, _, _, _ -> validateInputs() }
            etPassword.doOnTextChanged { _, _, _, _ -> validateInputs() }

            btnLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val rememberMeChecked = cbRememberMe.isChecked
                loginViewModel.login(email, password)

                if (rememberMeChecked) {
                    sessionManager.saveEmail(email)
                    sessionManager.saveRememberMe(true)
                } else {
                    sessionManager.clearSession()
                }
            }

            btnRegister.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
            }
        }
    }


    private fun validateInputs() {
        binding.apply {
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
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment4(email))
                    } else if (response != null) {
                        showSnackbar(getString(R.string.login_failed_please_try_again))
                    }
                }
            }
        }
    }

    private fun getResultFromRegister() {
        setFragmentResultListener("register_request") { _, bundle ->
            val email = bundle.getString("email")
            val password = bundle.getString("password")

            binding.etEmail.setText(email)
            binding.etPassword.setText(password)
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}

