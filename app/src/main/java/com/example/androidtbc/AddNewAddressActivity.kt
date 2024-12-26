package com.example.androidtbc

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtbc.databinding.ActivityAddNewAddressBinding
import com.google.android.material.snackbar.Snackbar

class AddNewAddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNewAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            saveAddress()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun saveAddress() {
        val streetAddress = binding.etStreet.text.toString().trim()
        val selectedRadioButtonId = binding.rg.checkedRadioButtonId

        if (streetAddress.isEmpty()) {
            binding.etStreet.error = "Street Address is required"
            Snackbar.make(binding.root, "Please fill in the Street Address", Snackbar.LENGTH_LONG).show()
            return
        }

        if (selectedRadioButtonId == -1) {
            Snackbar.make(binding.root, "Please select an address type (Home or Office)", Snackbar.LENGTH_LONG).show()
            return
        }



        val addressType = when (selectedRadioButtonId) {
            R.id.rbHome -> AddressType.Home
            R.id.rbOffice -> AddressType.Office
            else -> throw IllegalStateException("Invalid address type selected")
        }

        val newAddress = Address(
            id = System.currentTimeMillis().toInt(),
            type = addressType,
            street = streetAddress,
            isSelected = false
        )

        val resultIntent = Intent().apply {
            putExtra("new_address", newAddress)
        }
        setResult(RESULT_OK, resultIntent)

        finish()
    }
}