package com.example.androidtbc

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.androidtbc.FirstFragment.Companion.CARD_KEY
import com.example.androidtbc.FirstFragment.Companion.REQUEST_KEY
import com.example.androidtbc.databinding.FragmentAddNewCardBinding
import com.google.android.material.snackbar.Snackbar


class AddNewCardFragment :
    BaseFragment<FragmentAddNewCardBinding>(FragmentAddNewCardBinding::inflate) {

    override fun start() {
        setupRadioGroup()
        setupDefaultCard()
        setupTextChangedListeners()
        setupListeners()
        setupExpiryDateFormatting()
    }


    private fun setupListeners() {
        binding.btnBackButton.setOnClickListener {
            findNavController().popBackStack()

        }
        binding.btnAddCard.setOnClickListener {
            if (validateInputs()) {
                val cardType = when (binding.radioGroupCardType.checkedRadioButtonId) {
                    binding.radioMastercard.id -> CardType.MASTERCARD
                    binding.radioVisa.id -> CardType.VISA
                    else -> CardType.MASTERCARD
                }

                val card = Card(
                    name = binding.etCardholderName.text.toString(),
                    cardNumber = binding.etCardNumber.text.toString(),
                    validThru = binding.etExpires.text.toString(),
                    cvv = binding.etCVV.text.toString(),
                    type = cardType
                )


                val bundle = Bundle()
                bundle.putParcelable(CARD_KEY, card)
                setFragmentResult(REQUEST_KEY, bundle)

                Snackbar.make(binding.root, "Card added successfully", Snackbar.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }


    private fun setupDefaultCard() {
        updateCardType(CardType.MASTERCARD)
        binding.ivCard.tvValidThru.text = getString(R.string.mm_yy)
        binding.ivCard.tvCardHolderName.text = getString(R.string.full_name)
    }

    private fun setupRadioGroup() {
        binding.radioGroupCardType.check(binding.radioMastercard.id)

        binding.radioGroupCardType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.radioMastercard.id -> updateCardType(CardType.MASTERCARD)
                binding.radioVisa.id -> updateCardType(CardType.VISA)
            }
        }
    }

    private fun updateCardType(cardType: CardType) {
        when (cardType) {
            CardType.MASTERCARD -> {
                binding.ivCard.root.setBackgroundResource(R.drawable.bg_card_mastercard)
                binding.ivCard.ivVisaOrMastercard.setImageResource(R.drawable.mastercard)
            }

            CardType.VISA -> {
                binding.ivCard.root.setBackgroundResource(R.drawable.bg_card_visa)
                binding.ivCard.ivVisaOrMastercard.setImageResource(R.drawable.visa)
            }
        }
    }

    private fun setupTextChangedListeners() {
        binding.etCardholderName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.ivCard.tvCardHolderName.text = s?.toString() ?: "CARDHOLDER NAME"
            }
        })

        binding.etExpires.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.ivCard.tvValidThru.text = s?.toString() ?: "MM/YY"
            }
        })

        binding.etCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val formattedNumber =
                    s?.toString()?.chunked(4)?.joinToString(" ") ?: "**** **** **** ****"
                binding.ivCard.tvCardNumber.text = formattedNumber
            }
        })
    }

    private fun setupExpiryDateFormatting() {
        binding.etExpires.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return

                isUpdating = true

                val userInput = s?.toString()?.replace("/", "") ?: ""
                val formattedInput = when {
                    userInput.length <= 2 -> userInput
                    userInput.length <= 4 -> "${
                        userInput.substring(
                            0,
                            2
                        )
                    }/${userInput.substring(2)}"

                    else -> "${userInput.substring(0, 2)}/${userInput.substring(2, 4)}"
                }

                binding.etExpires.setText(formattedInput)
                binding.etExpires.setSelection(formattedInput.length)
                isUpdating = false
            }
        })
        binding.etExpires.inputType = InputType.TYPE_CLASS_NUMBER
        binding.etExpires.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(5))
    }



    private fun validateInputs(): Boolean {
        var isValid = true

        if (!CardValidator.isValidCardholderName(binding.etCardholderName.text.toString())) {
            binding.etCardholderName.error = "Required field"
            isValid = false
        }

        if (!CardValidator.isValidCardNumber(binding.etCardNumber.text.toString())) {
            binding.etCardNumber.error = "Invalid card number"
            isValid = false
        }

        if (!CardValidator.isValidExpiryDate(binding.etExpires.text.toString())) {
            binding.etExpires.error = "Invalid or expired expiry date"
            isValid = false
        }

        if (!CardValidator.isValidCvv(binding.etCVV.text.toString())) {
            binding.etCVV.error = "Invalid CVV"
            isValid = false
        }

        return isValid
    }

}