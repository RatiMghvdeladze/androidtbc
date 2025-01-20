package com.example.androidtbc.fragments

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androidtbc.viewModels.LoginViewModel
import com.example.androidtbc.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val viewModel: LoginViewModel by viewModels()

    override fun start() {
        setUpListeners()
        setUpObserver()
    }



    private fun setUpObserver(){
        lifecycleScope.launch(Dispatchers.Main) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.flowData.collect{
                    if(it?.isSuccessful == true){
                        Snackbar.make(binding.root, "Login Successfully", Snackbar.LENGTH_SHORT).show()
                    }else if(it?.isSuccessful == false){
                        val errorText = it.errorBody()?.string()
                        Snackbar.make(binding.root, errorText ?: "UnknownError", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
    private fun setUpListeners() {
        binding.btnLogin.setOnClickListener{
            with(binding) {
                val email = etUsername.text.toString()
                val password = etPassword.text.toString()
                val isValid = viewModel.validateEmail(email) && viewModel.validatePassword(password)
                if (isValid) {
                    viewModel.login(email, password)
                }else{
                    Snackbar.make(binding.root, "Invalid inputs", Snackbar.LENGTH_SHORT).show()
                }
            }
        }


    }
}