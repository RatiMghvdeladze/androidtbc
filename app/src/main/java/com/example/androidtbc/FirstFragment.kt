package com.example.androidtbc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.androidtbc.databinding.FragmentFirstBinding


class FirstFragment : Fragment() {
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    val usersList = mutableListOf(
        User(
            id = 1,
            firstName = "გრიშა",
            lastName = "ონიანი",
            birthday = "1724647601641",
            address = "სტალინის სახლმუზეუმი",
            email = "grisha@mail.ru"
        ),
        User(
            id = 2,
            firstName = "Jemal",
            lastName = "Kakauridze",
            birthday = "1714647601641",
            address = "თბილისი, ლილოს მიტოვებული ქარხანა",
            email = "jemal@gmail.com"
        ),
        User(
            id = 2,
            firstName = "Omger",
            lastName = "Kakauridze",
            birthday = "1724647701641",
            address = "თბილისი, ასათიანი 18",
            email = "omger@gmail.com"
        ),
        User(
            id = 32,
            firstName = "ბორის",
            lastName = "გარუჩავა",
            birthday = "1714947701641",
            address = "თბილისი, იაშვილი 14",
            email = ""
        ),
        User(
            id = 34,
            firstName = "აბთო",
            lastName = "სიხარულიძე",
            birthday = "1711947701641",
            address = "ფოთი",
            email = "tebzi@gmail.com",
            desc = null
        )
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }


    private fun init() {
        binding.etSearchUser.addTextChangedListener { text ->
            val chars = text.toString()
            if (chars.isNotEmpty()) {
                val foundUser = searchUser(chars)
                if (foundUser != null) {
                    binding.tvResult.text = getString(
                        R.string.id_name_birthday_address_email,
                        foundUser.id.toString(),
                        foundUser.firstName,
                        foundUser.lastName,
                        formatDate(foundUser.birthday.toLong()),
                        foundUser.address,
                        foundUser.email
                    )
                    binding.btnAddUser.visibility = View.GONE
                } else {
                    binding.tvResult.text = getString(R.string.user_not_found)
                    binding.btnAddUser.visibility = View.VISIBLE
                }
            } else {
                binding.tvResult.text = ""
                binding.btnAddUser.visibility = View.GONE
            }
        }

        binding.btnAddUser.setOnClickListener {
            goToSecondFragment()
        }
    }

    private fun goToSecondFragment() {
        val secondFragment = SsecondFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, secondFragment)
            .addToBackStack(null)
            .commit()

    }


    private fun formatDate(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("ddMMMMyyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }


    private fun searchUser(chars: String): User? {
        return usersList.find { user ->
            user.firstName.contains(chars, ignoreCase = true) ||
                    user.lastName.contains(chars, ignoreCase = true) ||
                    user.id.toString() == chars ||
                    user.birthday.contains(chars)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}