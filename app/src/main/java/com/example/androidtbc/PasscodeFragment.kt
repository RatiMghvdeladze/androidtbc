package com.example.androidtbc

import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androidtbc.databinding.FragmentPasscodeBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PasscodeFragment : BaseFragment<FragmentPasscodeBinding>(FragmentPasscodeBinding::inflate) {
    private val viewModel: PasscodeViewModel by viewModels()
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun start() {
        setUpListeners()
        observers()
        setUpBiometricAuth()
    }

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.enteredPasscode.collectLatest {
                    updateCircles(it.length)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isSuccess.collectLatest { isSuccess ->
                    when (isSuccess) {
                        true -> Snackbar.make(
                            binding.root,
                            getString(R.string.success), Snackbar.LENGTH_SHORT
                        ).show()

                        false -> Snackbar.make(
                            binding.root,
                            getString(R.string.incorrect_passcode), Snackbar.LENGTH_SHORT
                        )
                            .show()

                        else -> {}

                    }
                    viewModel.setNull()
                }
            }
        }
    }

    private fun updateCircles(count: Int) {
        with(binding) {
            val circlesList = listOf(ivCircle1, ivCircle2, ivCircle3, ivCircle4)
            circlesList.forEachIndexed { index, circle ->
                circle.isSelected = count > index
            }
        }

    }

    private fun setUpListeners() {
        with(binding) {
            btnOne1.setOnClickListener { viewModel.insertDigit("1") }
            btnTwo2.setOnClickListener { viewModel.insertDigit("2") }
            btnThree3.setOnClickListener { viewModel.insertDigit("3") }
            btnFour4.setOnClickListener { viewModel.insertDigit("4") }
            btnFive5.setOnClickListener { viewModel.insertDigit("5") }
            btnSix6.setOnClickListener { viewModel.insertDigit("6") }
            btnSeven7.setOnClickListener { viewModel.insertDigit("7") }
            btnEight8.setOnClickListener { viewModel.insertDigit("8") }
            btnNine9.setOnClickListener { viewModel.insertDigit("9") }
            btnZero0.setOnClickListener { viewModel.insertDigit("0") }
            btnDelete.setOnClickListener { viewModel.deleteLastDigit() }
        }
    }

    private fun setUpBiometricAuth() {
        val executor = ContextCompat.getMainExecutor(requireContext())

        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Snackbar.make(
                        binding.root,
                        getString(R.string.fingerprint_recognized), Snackbar.LENGTH_SHORT
                    )
                        .show()
                    viewModel.authenticateWithFingerprint()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Snackbar.make(
                        binding.root,
                        getString(R.string.authentication_error, errString),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Snackbar.make(
                        binding.root,
                        getString(R.string.fingerprint_not_recognized), Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.fingerprint_authentication))
            .setSubtitle(getString(R.string.use_your_fingerprint_to_authenticate))
            .setNegativeButtonText(getString(R.string.cancel))
            .build()

        binding.btnFingerprint.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }


}