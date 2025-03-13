package com.example.androidtbc.presentation.login

import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentLoginBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val viewModel: LoginViewModel by viewModels()

    override fun start() {
        setupListeners()
        observeViewState()
        observeEvents()
        setupFragmentResultListener()
        viewModel.processIntent(LoginIntent.CheckUserSession)
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
            etEmail.doAfterTextChanged { viewModel.processIntent(LoginIntent.ClearValidationErrors) }
            etPassword.doAfterTextChanged { viewModel.processIntent(LoginIntent.ClearValidationErrors) }

            btnLogin.setOnClickListener {
                viewModel.processIntent(
                    LoginIntent.LoginUser(
                        email = etEmail.text.toString(),
                        password = etPassword.text.toString(),
                        rememberMe = cbRememberMe.isChecked
                    )
                )
            }

            btnRegister.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
            }
        }
    }

    private fun observeViewState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    handleEvent(event)
                }
            }
        }
    }

    private fun updateUI(state: LoginViewState) {
        with(binding) {
            btnLogin.isEnabled = !state.isLoading
            btnLogin.text = if (state.isLoading) "" else getString(R.string.login)
            btnLoginProgress.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            showFieldError(state.emailError, etEmail)
            showFieldError(state.passwordError, etPassword)
        }
    }

    private fun showFieldError(errorMessage: String?, editText: View) {
        if (editText is androidx.appcompat.widget.AppCompatEditText) {
            editText.error = errorMessage
        }
    }

    private fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.ShowSnackbar -> {
                showSnackbar(event.message)
            }
            is LoginEvent.NavigateToHome -> {
                val currentDestination = findNavController().currentDestination?.id
                if (currentDestination != R.id.homeFragment) {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToHomeFragment(event.email)
                    )
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}