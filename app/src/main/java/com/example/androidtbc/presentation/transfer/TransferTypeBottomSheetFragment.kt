package com.example.androidtbc.presentation.transfer

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.example.androidtbc.R
import com.example.androidtbc.databinding.BottomSheetTransferTypeBinding
import com.example.androidtbc.domain.validators.AccountValidator
import com.example.androidtbc.presentation.model.ValidationTypeUI
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TransferTypeBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetTransferTypeBinding? = null
    private val binding get() = _binding!!

    private var onTypeSelected: ((ValidationTypeUI, String) -> Unit)? = null
    private var currentValidationType = ValidationTypeUI.ACCOUNT_NUMBER

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
        updateInputValidationHint()
    }

    private fun setupListeners() {
        with(binding) {
            rgTransferType.setOnCheckedChangeListener { _, checkedId ->
                currentValidationType = when (checkedId) {
                    R.id.rbAccountNumber -> ValidationTypeUI.ACCOUNT_NUMBER
                    R.id.rbPersonalId -> ValidationTypeUI.PERSONAL_ID
                    R.id.rbPhoneNumber -> ValidationTypeUI.PHONE_NUMBER
                    else -> ValidationTypeUI.ACCOUNT_NUMBER
                }

                updateInputValidationHint()
                etAccountInput.text?.clear()
                btnConfirm.isEnabled = false
                tilAccountInput.error = null
            }

            etAccountInput.doAfterTextChanged { text ->
                val input = text.toString()
                val isValid = validateInput(input)

                tilAccountInput.error = if (input.isNotEmpty() && !isValid) getErrorMessage() else null

                btnConfirm.isEnabled = isValid
            }

            btnConfirm.setOnClickListener {
                val input = etAccountInput.text.toString()
                if (validateInput(input)) {
                    onTypeSelected?.invoke(currentValidationType, input)
                } else {
                    tilAccountInput.error = getErrorMessage()
                }
            }
        }
    }

    private fun updateInputValidationHint() {
        with(binding) {
            tilAccountInput.hint = when (currentValidationType) {
                ValidationTypeUI.ACCOUNT_NUMBER -> getString(R.string.enter_23_symbol_account_number)
                ValidationTypeUI.PERSONAL_ID -> getString(R.string.enter_11_digit_personal_id)
                ValidationTypeUI.PHONE_NUMBER -> getString(R.string.enter_9_digit_phone_number)
            }

            etAccountInput.inputType = when (currentValidationType) {
                ValidationTypeUI.ACCOUNT_NUMBER -> InputType.TYPE_CLASS_TEXT
                ValidationTypeUI.PERSONAL_ID, ValidationTypeUI.PHONE_NUMBER -> InputType.TYPE_CLASS_NUMBER
            }
        }
    }

    private fun validateInput(input: String): Boolean {
        if (input.isEmpty()) return false

        return when (currentValidationType) {
            ValidationTypeUI.ACCOUNT_NUMBER -> AccountValidator.validateAccountNumber(input)
            ValidationTypeUI.PERSONAL_ID -> AccountValidator.validatePersonalId(input)
            ValidationTypeUI.PHONE_NUMBER -> AccountValidator.validatePhoneNumber(input)
        }
    }

    private fun getErrorMessage(): String = when (currentValidationType) {
        ValidationTypeUI.ACCOUNT_NUMBER -> getString(R.string.account_number_must_be_23_characters)
        ValidationTypeUI.PERSONAL_ID -> getString(R.string.personal_id_must_be_11_digits)
        ValidationTypeUI.PHONE_NUMBER -> getString(R.string.phone_number_must_be_9_digits)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(onTypeSelected: (ValidationTypeUI, String) -> Unit) =
            TransferTypeBottomSheetFragment().apply {
                this.onTypeSelected = onTypeSelected
            }
    }
}