package com.example.androidtbc.presentation.transfer

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.example.androidtbc.databinding.BottomSheetTransferTypeBinding
import com.example.androidtbc.domain.validators.AccountValidator
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TransferTypeBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetTransferTypeBinding? = null
    private val binding get() = _binding!!

    private var onTypeSelected: ((String, String) -> Unit)? = null
    private var currentValidationType = "ACCOUNT_NUMBER"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetTransferTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        updateInputValidationHint() // Set initial state
    }

    private fun setupListeners() {
        with(binding) {
            // Radio button selection logic
            rgTransferType.setOnCheckedChangeListener { _, checkedId ->
                currentValidationType = when {
                    view?.findViewById<View>(checkedId)?.let { it as? android.widget.RadioButton }?.text?.contains("Account Number", ignoreCase = true) == true -> "ACCOUNT_NUMBER"
                    view?.findViewById<View>(checkedId)?.let { it as? android.widget.RadioButton }?.text?.contains("Personal ID", ignoreCase = true) == true -> "PERSONAL_ID"
                    view?.findViewById<View>(checkedId)?.let { it as? android.widget.RadioButton }?.text?.contains("Phone Number", ignoreCase = true) == true -> "PHONE_NUMBER"
                    else -> "ACCOUNT_NUMBER"
                }
                updateInputValidationHint()
                etAccountInput.text?.clear()
                btnConfirm.isEnabled = false
            }

            // Input validation
            etAccountInput.doAfterTextChanged { text ->
                validateInput(text.toString())
            }

            // Confirm button
            btnConfirm.setOnClickListener {
                val input = etAccountInput.text.toString()
                if (validateInput(input)) {
                    onTypeSelected?.invoke(currentValidationType, input)
                }
            }
        }
    }

    private fun updateInputValidationHint() {
        with(binding) {
            // Set appropriate hint and input type
            tilAccountInput.hint = when (currentValidationType) {
                "ACCOUNT_NUMBER" -> "Enter 23 symbol account number"
                "PERSONAL_ID" -> "Enter 11 digit personal ID"
                "PHONE_NUMBER" -> "Enter 9 digit phone number"
                else -> "Enter account details"
            }

            etAccountInput.inputType = when (currentValidationType) {
                "ACCOUNT_NUMBER" -> InputType.TYPE_CLASS_TEXT
                "PERSONAL_ID", "PHONE_NUMBER" -> InputType.TYPE_CLASS_NUMBER
                else -> InputType.TYPE_CLASS_TEXT
            }
        }
    }

    private fun validateInput(input: String): Boolean {
        val isValid = when (currentValidationType) {
            "ACCOUNT_NUMBER" -> AccountValidator.validateAccountNumber(input)
            "PERSONAL_ID" -> AccountValidator.validatePersonalId(input)
            "PHONE_NUMBER" -> AccountValidator.validatePhoneNumber(input)
            else -> false
        }

        binding.tilAccountInput.error = if (isValid) null else "Invalid format"
        binding.btnConfirm.isEnabled = isValid
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(onTypeSelected: (String, String) -> Unit) =
            TransferTypeBottomSheetFragment().apply {
                this.onTypeSelected = onTypeSelected
            }
    }
}