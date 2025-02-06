package com.example.androidtbc.ui.passcode

import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androidtbc.R
import com.example.androidtbc.base.BaseFragment
import com.example.androidtbc.databinding.FragmentPasscodeBinding
import com.example.androidtbc.ui.passcode.adapter.PasscodeCircleAdapter
import com.example.androidtbc.viewmodel.PasscodeViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PasscodeFragment : BaseFragment<FragmentPasscodeBinding>(FragmentPasscodeBinding::inflate) {
    private val viewModel: PasscodeViewModel by viewModels()
    private lateinit var circleAdapter : PasscodeCircleAdapter
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun start() {
        setUpListeners()
        observers()
        setUpBiometricAuth()
        setUpRv()
    }

    private fun setUpRv() {
        circleAdapter = PasscodeCircleAdapter()
        with(binding) {
            rvPasscodeCircles.adapter = circleAdapter
            rvPasscodeCircles.itemAnimator = null
            circleAdapter.submitList(List(LENGTH_PASSCODE) { false })
        }
    }

    private fun updateCircles(count: Int) {
        circleAdapter.submitList(List(LENGTH_PASSCODE) { index -> index < count})
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
                        true -> showMessage(
                            getString(R.string.success))

                        false -> showMessage(
                            getString(R.string.incorrect_passcode)
                        )

                        else -> {}

                    }
                    viewModel.setNull()
                }
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
                    showMessage(
                        getString(R.string.fingerprint_recognized)
                    )
                    viewModel.authenticateWithFingerprint()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showMessage(
                        getString(R.string.authentication_error, errString),
                    )
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showMessage(getString(R.string.fingerprint_not_recognized))
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
    private fun showMessage(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(binding.root, message, duration).show()
    }

    companion object{
        private const val LENGTH_PASSCODE = PasscodeViewModel.LENGTH_PASSCODE
    }

}