package com.example.androidtbc.fragments

import androidx.navigation.fragment.findNavController
import com.example.androidtbc.databinding.FragmentWelcomeBinding

class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>(FragmentWelcomeBinding::inflate) {
    override fun start() {
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.btnRegister.setOnClickListener{
            findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToRegisterFragment())
        }
        binding.btnLogin.setOnClickListener{
            findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment())
        }
    }


}