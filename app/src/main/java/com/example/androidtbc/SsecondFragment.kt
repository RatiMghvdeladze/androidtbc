package com.example.androidtbc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.androidtbc.databinding.FragmentSsecondBinding

class SsecondFragment : Fragment() {
    private var _binding: FragmentSsecondBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSsecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            onSaveClick()
        }
    }

    private fun onSaveClick() {
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val birthday = binding.etBirthday.text.toString()
        val address = binding.etAddress.text.toString()
        val email = binding.etEmail.text.toString()
        val desc = binding.etDesc.text.toString()

        if (firstName.isEmpty() || lastName.isEmpty() || birthday.isEmpty() || address.isEmpty() || email.isEmpty()) {
            Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val newUser = User(
            id = (1..10000).random(),
            firstName = firstName,
            lastName = lastName,
            birthday = birthday,
            address = address,
            email = email,
            desc = desc.ifEmpty { null }
        )

        val firstFragment = parentFragmentManager.findFragmentById(R.id.container) as? FirstFragment
        firstFragment?.usersList?.add(newUser)

        Toast.makeText(requireContext(), "User added successfully (id = ${id})", Toast.LENGTH_SHORT)
            .show()
        parentFragmentManager.popBackStack()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
