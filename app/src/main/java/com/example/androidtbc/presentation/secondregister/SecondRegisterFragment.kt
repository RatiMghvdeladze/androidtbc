package com.example.androidtbc.presentation.secondregister

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentSecondRegisterBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SecondRegisterFragment : BaseFragment<FragmentSecondRegisterBinding>(FragmentSecondRegisterBinding::inflate) {
    private val viewModel: SecondRegisterViewModel by viewModels()
    private val args: SecondRegisterFragmentArgs by navArgs()

    override fun start() {
        setUpListeners()
        observeUserInfoState()
    }

    private fun observeUserInfoState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userInfoState.collectLatest { state ->
                    when (state) {
                        is Resource.Success -> {
                            val action =
                                SecondRegisterFragmentDirections.actionSecondRegisterFragmentToLoginFragment2(
                                    email = args.email,
                                    password = args.password
                                )
                            findNavController().navigate(action)

                            Snackbar.make(
                                binding.root,
                                getString(R.string.profile_information_saved_successfully),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            viewModel.resetState()
                        }

                        is Resource.Loading -> {
                            binding.btnContinue.isEnabled = false
                            binding.btnContinue.text = getString(R.string.loading_and_three_dot)
                        }

                        is Resource.Error -> {
                            binding.btnContinue.isEnabled = true
                            binding.btnContinue.text = getString(R.string.continuee)
                            Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_SHORT)
                                .show()
                        }

                        is Resource.Idle -> {
                            binding.btnContinue.isEnabled = true
                            binding.btnContinue.text = getString(R.string.continuee)
                        }
                    }
                }
            }
        }
    }

    private fun setUpListeners() {
        with(binding){
            btnContinue.setOnClickListener {
                val name = etYourName.text.toString().trim()
                val phoneNumber = etPhoneNumber.text.toString().trim()
                val city = etCity.text.toString().trim()

                if (validateInput(name, phoneNumber, city)) {
                    viewModel.saveUserInfo(name, phoneNumber, city)
                }
            }
        }
    }

    private fun validateInput(name: String, phoneNumber: String, city: String): Boolean {
        if (name.isEmpty()) {
            Snackbar.make(binding.root,
                getString(R.string.please_enter_your_name), Snackbar.LENGTH_SHORT).show()
            return false
        }

        if (phoneNumber.isEmpty()) {
            Snackbar.make(binding.root,
                getString(R.string.please_enter_your_phone_number), Snackbar.LENGTH_SHORT).show()
            return false
        }

        if (city.isEmpty()) {
            Snackbar.make(binding.root,
                getString(R.string.please_enter_your_city), Snackbar.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}