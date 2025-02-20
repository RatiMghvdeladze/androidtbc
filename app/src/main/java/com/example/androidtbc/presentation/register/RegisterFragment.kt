package com.example.androidtbc.presentation.register

import androidx.navigation.fragment.findNavController
import com.example.androidtbc.databinding.FragmentRegisterBinding
import com.example.androidtbc.presentation.base.BaseFragment

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {
    override fun start() {
      setUpListeners()
    }

    private fun setUpListeners() {
        with(binding){
            btnBack.setOnClickListener{
                findNavController().navigateUp()
            }
            btnSignIn.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}