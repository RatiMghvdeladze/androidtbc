package com.example.androidtbc.presentation.secondregister

import androidx.navigation.fragment.findNavController
import com.example.androidtbc.databinding.FragmentSecondRegisterBinding
import com.example.androidtbc.presentation.base.BaseFragment

class SecondRegisterFragment : BaseFragment<FragmentSecondRegisterBinding>(FragmentSecondRegisterBinding::inflate) {
    override fun start() {
        setUpListeners()

    }

    private fun setUpListeners() {
        with(binding){
            btnContinue.setOnClickListener {
                val action = SecondRegisterFragmentDirections.actionSecondRegisterFragmentToLoginFragment2()
                findNavController().navigate(action)
            }
        }
    }

}