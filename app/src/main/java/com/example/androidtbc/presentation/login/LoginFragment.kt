package com.example.androidtbc.presentation.login

import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentLoginBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.extension.launchLatest
import com.example.androidtbc.presentation.extension.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val viewModel: LoginViewModel by viewModels()

    override fun start() {
        setupListeners()
        observeViewState()
        observeEvents()
        setupFragmentResultListener()
        viewModel.onEvent(LoginEvent.CheckUserSession)
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
            etEmail.doAfterTextChanged { viewModel.onEvent(LoginEvent.ClearValidationErrors) }
            etPassword.doAfterTextChanged { viewModel.onEvent(LoginEvent.ClearValidationErrors) }
            btnLogin.setOnClickListener {
                viewModel.onEvent(
                    LoginEvent.LoginUser(
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
        launchLatest(viewModel.state) { state ->
            updateUI(state)
        }
    }

    private fun observeEvents() {
        launchLatest(viewModel.events) { event ->
            handleEvent(event)
        }
    }

    private fun updateUI(state: LoginState) {
        with(binding) {
            btnLogin.isEnabled = !state.isLoading
            btnLogin.text = if (state.isLoading) "" else getString(R.string.login)
            btnLoginProgress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            showFieldError(state.emailError, etEmail)
            showFieldError(state.passwordError, etPassword)
        }
    }

    private fun showFieldError(errorMessage: String?, editText: View) {
        if (editText is AppCompatEditText) {
            editText.error = errorMessage
        }
    }

    private fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.ShowSnackbar -> {
                binding.root.showSnackbar(event.message)
            }
            is LoginEvent.NavigateToHome -> {
                val currentDestination = findNavController().currentDestination?.id
                if (currentDestination != R.id.homeFragment) {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                    )
                }
            }
            is LoginEvent.LoginUser,
            is LoginEvent.ClearValidationErrors,
            is LoginEvent.CheckUserSession -> {}
        }
    }
}