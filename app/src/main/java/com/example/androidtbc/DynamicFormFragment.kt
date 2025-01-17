package com.example.androidtbc

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtbc.databinding.FragmentDynamicFormBinding
import com.google.android.material.snackbar.Snackbar

class DynamicFormFragment : BaseFragment<FragmentDynamicFormBinding>(
    FragmentDynamicFormBinding::inflate
) {
    private val viewModel: MainViewModel by viewModels()

    override fun start() {
        setUpRv()
        setUpRegisterButton()
    }

    private fun setUpRv() {
        binding.rvDynamic.adapter = OutAdapter(viewModel.obj) { fieldId, value ->
            viewModel.updateField(fieldId, value)
        }
        binding.rvDynamic.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setUpRegisterButton() {
        binding.btnRegister.setOnClickListener {
            val validationError = viewModel.validateForm()

            if (validationError != null) {
                Snackbar.make(binding.root, validationError, Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(binding.root, "Registered!!!", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}

