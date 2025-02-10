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
import com.example.androidtbc.utils.Resource
import com.example.androidtbc.utils.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private lateinit var loginViewModel: LoginViewModel

    override fun start() {
        initViewModel()
        checkSession()
        setupListeners()
        observeLoginState()
        setupFragmentResultListener()
    }

    private fun initViewModel() {
        loginViewModel = ViewModelProvider(this, ViewModelFactory {
            LoginViewModel(LocalDataStore(requireContext().applicationContext))
        })[LoginViewModel::class.java]
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

    private fun setupFragmentResultListener() {
        setFragmentResultListener("register_request") { _, bundle ->
            val email = bundle.getString("email")
            val password = bundle.getString("password")

            with(binding) {
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

    private fun observeLoginState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loginState.collect { state ->
                    handleLoginState(state)
                }
            }
        }
    }

    private fun handleLoginState(state: Resource<String>) {
        with(binding) {
            when (state) {
                is Resource.Idle -> {
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.login)
                    btnLoginProgress.visibility = View.GONE
                }
                is Resource.Loading -> {
                    btnLogin.isEnabled = false
                    btnLogin.text = ""
                    btnLoginProgress.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.login)
                    btnLoginProgress.visibility = View.GONE

                    showSnackbar("Successfully Logged In!")

                    val currentDestination = findNavController().currentDestination?.id
                    if (currentDestination != R.id.homeFragment) {
                        findNavController().navigate(
                            LoginFragmentDirections.actionLoginFragmentToHomeFragment(state.data)
                        )
                    }
                }
                is Resource.Error -> {
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.login)
                    btnLoginProgress.visibility = View.GONE
                    showSnackbar(state.errorMessage)
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}