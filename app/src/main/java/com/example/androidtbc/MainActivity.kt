package com.example.androidtbc

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtbc.databinding.ActivityMainBinding
import kotlin.collections.mutableListOf

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var inputValidator = InputValidator()

    private val usersList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.btnAddUser.setOnClickListener {
            addUser()
        }

        binding.btnGetUserInfo.setOnClickListener{
            getUserInfo()
        }
    }

    private fun getUserInfo() {
        val searchEmail = binding.etEnterUserEmail.text.toString()

        if (!inputValidator.isEmailValid(searchEmail)) {
            binding.tvFindUserFullName.visibility = View.GONE
            binding.tvFindUserEmail.visibility = View.GONE
            binding.tvUserNotFound.visibility = View.GONE

            binding.etEnterUserEmail.error = getString(R.string.invalid_format)
            return
        }
        val findUser: User? = usersList.find { it.email == searchEmail }
        if(findUser != null) {
            binding.tvUserNotFound.visibility = View.GONE

            binding.tvFindUserFullName.text =
                getString(R.string.fullName_of_searched_user, findUser.fullName)

            binding.tvFindUserEmail.text =
                getString(R.string.email_of_searched_user, findUser.email)

            binding.tvFindUserFullName.visibility = View.VISIBLE
            binding.tvFindUserEmail.visibility = View.VISIBLE


        }else{
            binding.tvFindUserFullName.visibility = View.GONE
            binding.tvFindUserEmail.visibility = View.GONE

            binding.tvUserNotFound.text = getString(R.string.user_not_found)
            binding.tvUserNotFound.visibility = View.VISIBLE
        }

    }
    private fun addUser() {
        val fullName = binding.etFullName.text.toString()
        val email = binding.etEmail.text.toString()

        if (!inputValidator.isFullNameValid(fullName)) {
            binding.tvUserAdded.visibility = View.GONE
            binding.etFullName.error = getString(R.string.invalid_format)
            return
        }
        if (!inputValidator.isEmailValid(email)) {
            binding.tvUserAdded.visibility = View.GONE
            binding.etEmail.error = getString(R.string.invalid_format)
            return
        }

        val findEmail = usersList.find{ it.email == email}

        if(findEmail == null) {
            val user = User(fullName,email)
            usersList.add(user)

            binding.tvUsersCounter.text = getString(R.string.users_counter, usersList.size.toString())

            binding.etFullName.text?.clear()
            binding.etEmail.text?.clear()

            binding.tvUserAdded.text = getString(R.string.user_added_successfully)
            binding.tvUserAdded.visibility = View.VISIBLE
        }else{
            binding.tvUserAdded.text = getString(R.string.email_is_already_taken)
            binding.tvUserAdded.visibility = View.VISIBLE
        }


    }

}