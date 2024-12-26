package com.example.androidtbc

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtbc.databinding.ActivityEditAddressBinding
import com.google.android.material.snackbar.Snackbar

class EditAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditAddressBinding
    private var address: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)


        address = getIntentAddress()
        if (address == null) {
            Snackbar.make(findViewById(android.R.id.content), "Error loading address", Snackbar.LENGTH_SHORT).show()
            finish()
            return
        }
        loadAddressData()
        setupListeners()
    }



    private fun getIntentAddress(): Address? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("address", Address::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("address")
        }
    }



    private fun loadAddressData() {
        address?.let { addr ->
            binding.apply {
                etStreet.setText(addr.street)
                when (addr.type) {
                    AddressType.Home -> rbHome.isChecked = true
                    AddressType.Office -> rbOffice.isChecked = true
                }
            }
        }
    }



    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            saveChanges()
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
    }




    private fun saveChanges() {
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

        address?.let {
            val updatedAddress = it.copy(
                street = streetAddress,
                type = when (selectedRadioButtonId) {
                    R.id.rbHome -> AddressType.Home
                    R.id.rbOffice -> AddressType.Office
                    else -> it.type
                }
            )

            setResult(RESULT_OK, Intent().apply {
                putExtra("address", updatedAddress)
            })
            finish()
        }
    }




}
