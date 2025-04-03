package com.example.androidtbc.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidtbc.presentation.home.HomeEvent
import com.example.androidtbc.presentation.home.HomeScreen
import com.example.androidtbc.presentation.home.HomeViewModel
import com.example.androidtbc.presentation.login.LoginEvent
import com.example.androidtbc.presentation.login.LoginScreen
import com.example.androidtbc.presentation.login.LoginViewModel
import com.example.androidtbc.presentation.profile.ProfileEvent
import com.example.androidtbc.presentation.profile.ProfileScreen
import com.example.androidtbc.presentation.profile.ProfileViewModel
import com.example.androidtbc.presentation.register.RegisterEvent
import com.example.androidtbc.presentation.register.RegisterScreen
import com.example.androidtbc.presentation.register.RegisterViewModel
import com.example.androidtbc.presentation.utils.HandleEvents
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Profile : Screen("profile")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val showSnackbar: (String) -> Unit = { message ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            startDestination = startDestination
        ) {
            composable(Screen.Login.route) {
                val viewModel: LoginViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()

                val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
                val email = remember { savedStateHandle?.get<String>("email") }
                val password = remember { savedStateHandle?.get<String>("password") }

                LaunchedEffect(email, password) {
                    if (email != null) {
                        savedStateHandle?.remove<String>("email")
                    }
                    if (password != null) {
                        savedStateHandle?.remove<String>("password")
                    }
                }

                LaunchedEffect(key1 = Unit) {
                    viewModel.onEvent(LoginEvent.CheckUserSession)
                }

                HandleEvents(flow = viewModel.events) { event ->
                    when (event) {
                        is LoginEvent.NavigateToHome -> {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        is LoginEvent.ShowSnackbar -> {
                            showSnackbar(event.message)
                        }
                        else -> {}
                    }
                }

                LoginScreen(
                    state = state,
                    email = email,
                    password = password,
                    onLogin = { emailInput, passwordInput, rememberMe ->
                        viewModel.onEvent(LoginEvent.LoginUser(emailInput, passwordInput, rememberMe))
                    },
                    onRegisterClick = {
                        navController.navigate(Screen.Register.route)
                    },
                    onEmailChange = { email ->
                        viewModel.validateEmail(email)
                    },
                    onPasswordChange = { password ->
                        viewModel.validatePassword(password)
                    }
                )
            }

            composable(Screen.Register.route) {
                val viewModel: RegisterViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    viewModel.onEvent(RegisterEvent.ClearValidationErrors)
                }

                HandleEvents(flow = viewModel.events) { event ->
                    when (event) {
                        is RegisterEvent.NavigateBack -> {
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "email",
                                event.email
                            )
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "password",
                                event.password
                            )
                            navController.popBackStack()
                        }
                        is RegisterEvent.ShowSnackbar -> {
                            showSnackbar(event.message)
                        }
                        else -> {}
                    }
                }

                RegisterScreen(
                    state = state,
                    onRegister = { email, password, repeatPassword ->
                        viewModel.onEvent(
                            RegisterEvent.RegisterUser(
                                email, password, repeatPassword
                            )
                        )
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onEmailChange = { email ->
                        viewModel.validateEmail(email)
                    },
                    onPasswordChange = { password ->
                        viewModel.validatePassword(password)
                    },
                    onRepeatPasswordChange = { password, repeatPassword ->
                        viewModel.validateRepeatPassword(password, repeatPassword)
                    }
                )
            }

            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    viewModel.onEvent(HomeEvent.LoadUsers)
                }

                HandleEvents(flow = viewModel.events) { event ->
                    when (event) {
                        is HomeEvent.NavigateToProfile -> {
                            navController.navigate(Screen.Profile.route)
                        }
                        is HomeEvent.ShowSnackbar -> {
                            showSnackbar(event.message)
                        }
                        else -> {}
                    }
                }

                HomeScreen(
                    state = state,
                    usersFlow = viewModel.users,
                    onNavigateToProfile = {
                        viewModel.onEvent(HomeEvent.NavigateToProfile)
                    },
                    onRetry = {
                        viewModel.onEvent(HomeEvent.RetryLoading)
                    }
                )
            }

            composable(Screen.Profile.route) {
                val viewModel: ProfileViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    viewModel.onEvent(ProfileEvent.CheckSessionStatus)
                    viewModel.onEvent(ProfileEvent.LoadUserEmail)
                }

                HandleEvents(flow = viewModel.events) { event ->
                    when (event) {
                        is ProfileEvent.NavigateToLogin -> {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                        is ProfileEvent.ShowSnackbar -> {
                            showSnackbar(event.message)
                        }
                        else -> {}
                    }
                }

                ProfileScreen(
                    state = state,
                    onLogout = {
                        viewModel.onEvent(ProfileEvent.LogoutUser)
                    }
                )
            }
        }
    }
}