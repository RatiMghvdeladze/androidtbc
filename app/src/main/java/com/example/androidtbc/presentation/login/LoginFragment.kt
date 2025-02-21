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
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentLoginBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.utils.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("GoogleSignIn", "Result received with code: ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                Log.d("GoogleSignIn", "Attempting to get Google account")
                val account = task.getResult(ApiException::class.java)
                Log.d("GoogleSignIn", "Got account: ${account?.email}")
                account?.idToken?.let { token ->
                    Log.d("GoogleSignIn", "Got ID token, attempting sign in")
                    loginViewModel.signInWithGoogle(token)
                } ?: run {
                    Log.e("GoogleSignIn", "ID token was null")
                    Snackbar.make(binding.root, "Failed to get Google credentials", Snackbar.LENGTH_LONG).show()
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Sign-In Failed. Status code: ${e.statusCode}", e)
                Snackbar.make(
                    binding.root,
                    "Google sign in failed: ${e.statusMessage ?: e.localizedMessage}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        } else {
            Log.e("GoogleSignIn", "Sign-in result was not OK: ${result.resultCode}")
        }
    }

    override fun start() {
        googleSignInClient = loginViewModel.getGoogleSignInClient()
        setUpListeners()
        observeLoginState()
    }

    private fun observeLoginState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loginState.collect { state ->
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
                            // Navigate to main screen or home
                            val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                            findNavController().navigate(action)
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
                loginViewModel.signIn(email, password)
            }

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
