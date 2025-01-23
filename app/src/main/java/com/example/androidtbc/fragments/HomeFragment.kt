package com.example.androidtbc.fragments

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.androidtbc.LocalDataStore
import com.example.androidtbc.ViewModelFactory
import com.example.androidtbc.databinding.FragmentHomeBinding
import com.example.androidtbc.viewModels.HomeViewModel
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val args: HomeFragmentArgs by navArgs()
    private lateinit var homeViewModel: HomeViewModel

    override fun start() {
        initViewModel()
        displayEmail()

        binding.btnLogOut.setOnClickListener {
            homeViewModel.clearUserData()
            observer()
        }
    }

    private fun observer(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                homeViewModel.getEmail().collect{
                    if(it == null){
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                    }
                }
            }
        }

    }

    private fun initViewModel() {
        homeViewModel = ViewModelProvider(this, ViewModelFactory {
            HomeViewModel(LocalDataStore(requireContext().applicationContext))
        })[HomeViewModel::class.java]
    }

    private fun displayEmail() {
        val email = args.email
        binding.tvYourEmail.text = email
    }
}