package com.example.androidtbc.presentation.register

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentRegisterBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.utils.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {
    private val registerViewModel: RegisterViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { token ->
                    registerViewModel.signUpWithGoogle(token)
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Sign-In Failed", e)
                Snackbar.make(
                    binding.root,
                    "Google sign in failed: ${e.localizedMessage}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun start() {
        googleSignInClient = registerViewModel.getGoogleSignInClient()
        setUpListeners()
        observeRegisterState()
    }

    private fun observeRegisterState() {
        viewLifecycleOwner.lifecycleScope.launch {
            registerViewModel.registerState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.btnSignUp.isEnabled = false
                        binding.progressBarSignUp.visibility = View.VISIBLE
                        binding.btnSignUp.text = ""
                    }
                    is Resource.Success -> {
                        binding.btnSignUp.isEnabled = true
                        binding.progressBarSignUp.visibility = View.GONE
                        binding.btnSignUp.text = getString(R.string.sign_up)

                        val email = binding.etEmail.text.toString()
                        val password = binding.etPassword.text.toString()

                        registerViewModel.setUserCredentials(email, password)

                        val action = RegisterFragmentDirections.actionRegisterFragmentToSecondRegisterFragment(
                            email = email,
                            password = password
                        )
                        findNavController().navigate(action)

                        registerViewModel.resetState()
                    }
                    is Resource.Error -> {
                        binding.btnSignUp.isEnabled = true
                        binding.progressBarSignUp.visibility = View.GONE
                        binding.btnSignUp.text = getString(R.string.sign_up)
                        Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_SHORT).show()
                    }
                    is Resource.Idle -> {
                        binding.btnSignUp.isEnabled = true
                        binding.progressBarSignUp.visibility = View.GONE
                        binding.btnSignUp.text = getString(R.string.sign_up)
                    }
                }
            }
        }
    }


    private fun setUpListeners() {
        with(binding) {
            btnSignUp.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val repeatPassword = etRepeatPassword.text.toString()

                registerViewModel.signUp(email, password, repeatPassword)
            }

            btnSignIn.setOnClickListener {
                findNavController().navigateUp()
            }

            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            btnGoogle.setOnClickListener {
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            }
        }
    }
}