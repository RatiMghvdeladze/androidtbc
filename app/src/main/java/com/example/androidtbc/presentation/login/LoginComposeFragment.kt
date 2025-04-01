package com.example.androidtbc.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.R
import com.example.androidtbc.presentation.extension.launchLatest
import com.example.androidtbc.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginComposeFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    private var emailFromRegister: String? = null
    private var passwordFromRegister: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setLoginContent()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkForRegistrationData()
        observeLoginEvents()
        viewModel.onEvent(LoginEvent.CheckUserSession)
    }

    private fun checkForRegistrationData() {
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            get<String>("email")?.let { email ->
                emailFromRegister = email
                remove<String>("email")
                view?.invalidate()
            }

            get<String>("password")?.let { password ->
                passwordFromRegister = password
                remove<String>("password")
                view?.invalidate()
            }
        }
    }

    private fun observeLoginEvents() {
        launchLatest(viewModel.events) { event ->
            when (event) {
                is LoginEvent.NavigateToHome -> {
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
                is LoginEvent.ShowSnackbar -> {
                    Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun ComposeView.setLoginContent() {
        setContent {
            val state = viewModel.state.collectAsState().value
            AppTheme {
                LoginScreen(
                    state = state,
                    email = emailFromRegister,
                    password = passwordFromRegister,
                    onLogin = { email, password, rememberMe ->
                        viewModel.onEvent(LoginEvent.LoginUser(email, password, rememberMe))
                    },
                    onRegisterClick = {
                        emailFromRegister = null
                        passwordFromRegister = null
                        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (view as? ComposeView)?.setLoginContent()
    }
}