package com.example.androidtbc.presentation.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
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
    }

    private fun setupListeners() {
        binding.rgTransferType.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = view?.findViewById<RadioButton>(checkedId)
            currentValidationType = when {
                radioButton?.text?.contains("Account Number", ignoreCase = true) == true -> "ACCOUNT_NUMBER"
                radioButton?.text?.contains("Personal ID", ignoreCase = true) == true -> "PERSONAL_ID"
                radioButton?.text?.contains("Phone Number", ignoreCase = true) == true -> "PHONE_NUMBER"
                else -> "ACCOUNT_NUMBER"
            }

            // Update input validation hint and clear field
            updateInputValidationHint()
            binding.etAccountInput.text?.clear()
            binding.btnConfirm.isEnabled = false
        }

        binding.etAccountInput.doAfterTextChanged { text ->
            validateInput(text.toString())
        }

        binding.btnConfirm.setOnClickListener {
            val input = binding.etAccountInput.text.toString()
            if (validateInput(input)) {
                onTypeSelected?.invoke(currentValidationType, input)
            }
        }

        // Set initial state
        updateInputValidationHint()
    }

    private fun updateInputValidationHint() {
        val hint = when (currentValidationType) {
            "ACCOUNT_NUMBER" -> "Enter 23 symbol account number"
            "PERSONAL_ID" -> "Enter 11 digit personal ID"
            "PHONE_NUMBER" -> "Enter 9 digit phone number"
            else -> "Enter account details"
        }
        binding.tilAccountInput.hint = hint

        // Update input type
        binding.etAccountInput.inputType = when (currentValidationType) {
            "ACCOUNT_NUMBER" -> android.text.InputType.TYPE_CLASS_TEXT
            "PERSONAL_ID", "PHONE_NUMBER" -> android.text.InputType.TYPE_CLASS_NUMBER
            else -> android.text.InputType.TYPE_CLASS_TEXT
        }
    }

    private fun validateInput(input: String): Boolean {
        val isValid = when (currentValidationType) {
            "ACCOUNT_NUMBER" -> AccountValidator.validateAccountNumber(input)
            "PERSONAL_ID" -> AccountValidator.validatePersonalId(input)
            "PHONE_NUMBER" -> AccountValidator.validatePhoneNumber(input)
            else -> false
        }

        if (isValid) {
            binding.tilAccountInput.error = null
        } else {
            binding.tilAccountInput.error = "Invalid format"
        }

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