package com.example.androidtbc.fragments

import androidx.navigation.fragment.findNavController
import com.example.androidtbc.SessionManager
import com.example.androidtbc.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private lateinit var sessionManager: SessionManager

    override fun start() {
        sessionManager = SessionManager(requireContext())
        displayEmail()

        binding.btnLogOut.setOnClickListener {
            sessionManager.clearSession()
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
        }
    }

    private fun displayEmail() {
        // პირველად ვცდილობთ არგუმენტებიდან წამოღებას
        val emailFromArgs = arguments?.getString("email")

        // თუ არგუმენტებში არ არის, მაშინ ვიღებთ სესიიდან
        val email = emailFromArgs ?: sessionManager.getEmail() ?: "No Email Found"

        binding.tvYourEmail.text = email

        // თუ email მოვიდა არგუმენტებიდან და "Remember me" ჩართული იყო, ვინახავთ სესიაშიც
        if (emailFromArgs != null) {
            sessionManager.saveEmail(emailFromArgs)
        }
    }
}