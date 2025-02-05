package com.example.androidtbc

import android.util.Log.d
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androidtbc.databinding.FragmentPasscodeBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PasscodeFragment : BaseFragment<FragmentPasscodeBinding>(FragmentPasscodeBinding::inflate) {
    private val viewModel: PasscodeViewModel by viewModels()

    override fun start() {
        setUpListeners()
        observers()
    }

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.enteredPasscode.collectLatest{
                    d("rame", "${it.length}")
                    d("rame", it)
                    updateCircles(it.length)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.isSuccess.collectLatest{ isSuccess ->
                   when(isSuccess){
                       true -> Snackbar.make(binding.root, "Success", Snackbar.LENGTH_SHORT).show()
                       false -> Snackbar.make(binding.root, "Failure", Snackbar.LENGTH_SHORT).show()
                       else -> {}

                   }
                    viewModel.setNull()
                }
            }
        }
    }

    private fun updateCircles(count: Int){
        with(binding) {
            val circlesList = listOf(ivCircle1, ivCircle2, ivCircle3, ivCircle4)
            circlesList.forEachIndexed { index, circle ->
                circle.isSelected = count > index
            }
        }

    }

    private fun setUpListeners() {
        with(binding){
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
            btnDelete.setOnClickListener{viewModel.deleteLastDigit()}
        }
    }


}