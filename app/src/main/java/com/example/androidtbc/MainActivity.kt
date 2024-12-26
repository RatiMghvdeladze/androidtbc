package com.example.androidtbc

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtbc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var addressAdapter: AddressListAdapter

    private val addAddressResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val address = if (Build.VERSION.SDK_INT >= 33) {
                result.data?.getParcelableExtra("new_address", Address::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra("new_address")
            }
            address?.let { addressAdapter.addAddress(it) }
        }
    }

    private val editAddressResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val editedAddress = if (Build.VERSION.SDK_INT >= 33) {
                result.data?.getParcelableExtra("address", Address::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra("address")
            }
            editedAddress?.let { address ->
                val currentList = addressAdapter.currentList.toMutableList()
                val position = currentList.indexOfFirst { it.id == address.id }
                if (position != -1) {
                    currentList[position] = address
                    addressAdapter.submitList(currentList)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRv()
        setUpListeners()
        setInitialAddresses()
    }


    private fun setUpRv() {
        addressAdapter = AddressListAdapter().apply {
            setOnEditClickListener { address ->
                editAddressResult.launch(
                    Intent(this@MainActivity, EditAddressActivity::class.java).apply {
                        putExtra("address", address)
                    }
                )
            }

            setOnItemLongClickListener { address ->
                addressAdapter.deleteAddress(address)
            }
        }

        binding.rvAddress.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = addressAdapter
        }
    }




    private fun setUpListeners() {
        binding.btnAddNewAddress.setOnClickListener {
            addAddressResult.launch(Intent(this, AddNewAddressActivity::class.java))
        }
    }




    private fun setInitialAddresses() {
        addressAdapter.submitList(
            listOf(
                Address(1, AddressType.Office, "SBI Building, street 3, Software Park", true ),
                Address(2, AddressType.Home, "SBI Building, street 3, Software Park")
            )
        )
    }



}