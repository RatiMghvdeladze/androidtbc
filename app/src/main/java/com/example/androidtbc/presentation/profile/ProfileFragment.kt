package com.example.androidtbc.presentation.profile

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.androidtbc.data.local.LocalDataStore
import com.example.androidtbc.utils.ViewModelFactory
import com.example.androidtbc.databinding.FragmentProfileBinding
import com.example.androidtbc.presentation.base.BaseFragment
import kotlinx.coroutines.launch

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {
    private val args: ProfileFragmentArgs by navArgs()
    private lateinit var profileViewModel: ProfileViewModel

    override fun start() {
        initViewModel()
        displayEmail()

        binding.btnLogOut.setOnClickListener {
            profileViewModel.clearUserData()
            observer()
        }
    }

    private fun observer(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                profileViewModel.getEmail().collect{
                    if(it == null){
                        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToLoginFragment())
                    }
                }
            }
        }

    }

    private fun initViewModel() {
        profileViewModel = ViewModelProvider(this, ViewModelFactory {
            ProfileViewModel(LocalDataStore(requireContext().applicationContext))
        })[ProfileViewModel::class.java]
    }

    private fun displayEmail() {
        val email = args.email
        binding.tvYourEmail.text = email
    }
}