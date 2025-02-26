package com.example.androidtbc.presentation.login

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val args: LoginFragmentArgs by navArgs()
    private val TAG = "LoginFragment"

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "Google sign-in result received with code: ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "Got Google account: ${account?.email}")
                account?.idToken?.let { token ->
                    // Pass the remember me state to the ViewModel
                    loginViewModel.signInWithGoogle(token, binding.cbRememberMe.isChecked)
                } ?: run {
                    Log.e(TAG, "ID token was null")
                    Snackbar.make(binding.root, "Failed to get Google credentials", Snackbar.LENGTH_LONG).show()
                }
            } catch (e: ApiException) {
                Log.e(TAG, "Sign-In Failed. Status code: ${e.statusCode}", e)
                Snackbar.make(
                    binding.root,
                    "Google sign in failed: ${e.statusMessage ?: e.localizedMessage}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        } else {
            Log.e(TAG, "Google sign-in result was not OK: ${result.resultCode}")
        }
    }

// Only showing the parts that need to be updated in LoginFragment.kt

    override fun start() {
        Log.d(TAG, "LoginFragment started, fromRegistration: ${args.fromRegistration}, email: ${args.email}")
        googleSignInClient = loginViewModel.getGoogleSignInClient()

        // Ensure the binding is initialized before accessing views
        binding.etEmail.setText(args.email)
        binding.etPassword.setText(args.password)

        // Check if we're coming from registration
        if (args.fromRegistration) {
            // Show success message
            Snackbar.make(binding.root, "Registration complete! You can now log in.", Snackbar.LENGTH_LONG).show()
        }

        setUpListeners()
        observeLoginState()
        checkIfAlreadyLoggedIn()
    }


    // Check if user is already logged in with Remember Me
// In LoginFragment.kt's checkIfAlreadyLoggedIn() function
    private fun checkIfAlreadyLoggedIn() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // In checkIfAlreadyLoggedIn() in LoginFragment.kt
                loginViewModel.isUserLoggedIn.collectLatest { isLoggedIn ->
                    Log.d(TAG, "User logged in status: $isLoggedIn")
                    // Only navigate if we came here directly, not from logout
                    if (isLoggedIn && !args.fromLogout) {
                        safeNavigateToHome("Auto login")
                    }
                }
            }
        }
    }

    private fun safeNavigateToHome(source: String) {
            val navController = findNavController()
            if (navController.currentDestination?.id == R.id.loginFragment) {
                Log.d(TAG, "Navigating to home from: $source")
                val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                navController.navigate(action)
            }

    }

    private fun observeLoginState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loginState.collectLatest { state ->
                    when (state) {
                        is Resource.Loading -> {
                            binding.btnSignIn.isEnabled = false
                            binding.progressBarSignIn.visibility = View.VISIBLE
                            binding.btnSignIn.text = ""
                        }

                        is Resource.Success -> {
                            binding.btnSignIn.isEnabled = true
                            binding.progressBarSignIn.visibility = View.GONE
                            binding.btnSignIn.text = getString(R.string.sign_in)
                            safeNavigateToHome("Login success")
                            Snackbar.make(binding.root, "Successfully logged in", Snackbar.LENGTH_SHORT).show()
                            loginViewModel.resetState()
                        }

                        is Resource.Error -> {
                            binding.btnSignIn.isEnabled = true
                            binding.progressBarSignIn.visibility = View.GONE
                            binding.btnSignIn.text = getString(R.string.sign_in)
                            Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_SHORT).show()
                        }

                        is Resource.Idle -> {
                            binding.btnSignIn.isEnabled = true
                            binding.progressBarSignIn.visibility = View.GONE
                            binding.btnSignIn.text = getString(R.string.sign_in)
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

            // Rest of the code remains the same
            btnSignUp.setOnClickListener {
                val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                findNavController().navigate(action)
            }

            btnGoogle.setOnClickListener {
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            }
        }
    }

}