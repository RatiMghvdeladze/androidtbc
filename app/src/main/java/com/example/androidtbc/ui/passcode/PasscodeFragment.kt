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
import com.example.androidtbc.ui.passcode.adapter.PasscodeButtonAdapter
import com.example.androidtbc.ui.passcode.adapter.PasscodeCircleAdapter
import com.example.androidtbc.viewmodel.PasscodeViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PasscodeFragment : BaseFragment<FragmentPasscodeBinding>(FragmentPasscodeBinding::inflate) {
    private val viewModel: PasscodeViewModel by viewModels()

    private lateinit var circleAdapter: PasscodeCircleAdapter
    private lateinit var passcodeButtonAdapter: PasscodeButtonAdapter

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun start() {
        setUpCircles()
        setUpListeners()
        setUpBiometricAuth()
        observers()
    }

    private fun setUpCircles() {
        val spacing = resources.getDimensionPixelSize(R.dimen.size40)

        circleAdapter = PasscodeCircleAdapter()
        with(binding.rvPasscodeCircles) {
            adapter = circleAdapter
            addItemDecoration(GridSpacingItemDecoration(4, spacing))
            itemAnimator = null
            circleAdapter.submitList(List(viewModel.getPasscodeLength()) { false })
        }
    }


    private fun setUpListeners() {
        val spacing = resources.getDimensionPixelSize(R.dimen.size20)
        passcodeButtonAdapter = PasscodeButtonAdapter { key ->
            when (key) {
                "fingerprint" -> biometricPrompt.authenticate(promptInfo)
                "delete" -> viewModel.deleteLastDigit()
                else -> viewModel.insertDigit(key)
            }
        }

        with(binding.rvKeypad) {
            adapter = passcodeButtonAdapter
            addItemDecoration(GridSpacingItemDecoration(3, spacing))
        }
    }

    private fun updateCircles(count: Int) {
        circleAdapter.submitList(List(viewModel.getPasscodeLength()) { index -> index < count })
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
                viewModel.uiEvent.collectLatest { message ->
                    showMessage(message)
                }
            }
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
                    showMessage(getString(R.string.fingerprint_recognized))
                    viewModel.authenticateWithFingerprint()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showMessage(getString(R.string.authentication_error, errString))
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showMessage(getString(R.string.fingerprint_not_recognized))
                }
            }
        )

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.fingerprint_authentication))
            .setSubtitle(getString(R.string.use_your_fingerprint_to_authenticate))
            .setNegativeButtonText(getString(R.string.cancel))
            .build()
    }

    private fun showMessage(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(binding.root, message, duration).show()
    }

}