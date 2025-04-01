package com.example.androidtbc.presentation.register

import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentRegisterBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.extension.launchLatest
import com.example.androidtbc.presentation.extension.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {
    private val viewModel: RegisterViewModel by viewModels()

    override fun start() {
        setupListeners()
        observeViewState()
        observeEvents()
    }

    private fun setupListeners() {
        with(binding) {
            etEmail.doAfterTextChanged { viewModel.onEvent(RegisterEvent.ClearValidationErrors) }
            etPassword.doAfterTextChanged { viewModel.onEvent(RegisterEvent.ClearValidationErrors) }
            etRepeatPassword.doAfterTextChanged { viewModel.onEvent(RegisterEvent.ClearValidationErrors) }
            btnRegister.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                viewModel.onEvent(
                    RegisterEvent.RegisterUser(
                        email = email,
                        password = password,
                        repeatPassword = etRepeatPassword.text.toString()
                    )
                )
            }
            btnBack.setOnClickListener {
                findNavController().popBackStack()
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

    private fun updateUI(state: RegisterState) {
        with(binding) {
            btnRegister.isEnabled = !state.isLoading
            btnRegister.text = if (state.isLoading) "" else getString(R.string.register)
            btnRegisterProgress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            showFieldError(state.emailError, etEmail)
            showFieldError(state.passwordError, etPassword)
            showFieldError(state.repeatPasswordError, etRepeatPassword)
        }
    }

    private fun showFieldError(errorMessage: String?, editText: View) {
        if (editText is AppCompatEditText) {
            editText.error = errorMessage
        }
    }

    private fun handleEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.ShowSnackbar -> {
                binding.root.showSnackbar(event.message)
            }
            is RegisterEvent.NavigateBack -> {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("email", event.email)
                findNavController().previousBackStackEntry?.savedStateHandle?.set("password", event.password)

                findNavController().popBackStack()
            }
            is RegisterEvent.RegisterUser,
            is RegisterEvent.ClearValidationErrors -> {}
        }
    }
}