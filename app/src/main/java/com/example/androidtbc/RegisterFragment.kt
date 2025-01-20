package com.example.androidtbc

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androidtbc.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    private val viewModel: RegisterViewModel by viewModels()

    override fun start() {
        setUpListeners()
        setUpObserver()
    }



    private fun setUpObserver(){
        lifecycleScope.launch(Dispatchers.Main) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.flowData.collect{
                    if(it?.isSuccessful == true){
                        Snackbar.make(binding.root, "Register Successfully", Snackbar.LENGTH_SHORT).show()
                    }else if(it?.isSuccessful == false){
                        val errorText = it.errorBody()?.string()
                        Snackbar.make(binding.root, errorText ?: "UnknownError", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
    private fun setUpListeners() {
        binding.btnRegister.setOnClickListener{
            with(binding) {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val isValid = viewModel.validateEmail(email) && viewModel.validatePassword(password)
                if (isValid) {
                    viewModel.register(email, password)
                }else{
                    Snackbar.make(binding.root, "Invalid inputs", Snackbar.LENGTH_SHORT).show()
                }
            }
        }


    }


}