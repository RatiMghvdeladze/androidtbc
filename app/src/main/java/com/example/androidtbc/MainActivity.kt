package com.example.androidtbc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtbc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
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

        binding.btnAddUser.setOnClickListener{
            addUser()
        }

        binding.btnRemoveUser.setOnClickListener{
            removeUser()
        }

        binding.btnUpdateUser.setOnClickListener{
            updateUser()
        }


    }
    private fun updateUser(){
        val firstName = binding.etFirstname.text.toString()
        val lastName = binding.etLastname.text.toString()
        val age = binding.etAge.text.toString()
        val email = binding.etEmail.text.toString()

        if(firstName.isEmpty() || lastName.isEmpty() || age.isEmpty() || email.isEmpty()){
            showMessage("Please fill all fields to update", false)
            return
        }
        val findUser = usersList.find{it.email == email}
        if(findUser != null){
            if(firstName.isNotEmpty()) findUser.firstName = firstName
            if(lastName.isNotEmpty()) findUser.lastName = lastName
            if(age.isNotEmpty()) findUser.age = age.toInt()

            showMessage("User updated successfully", true)
        }else{
            showMessage("User not found", false)
        }

    }

    private fun removeUser(){
        val firstName = binding.etFirstname.text.toString()
        val lastName = binding.etLastname.text.toString()
        val age = binding.etAge.text.toString()
        val email = binding.etEmail.text.toString()

        if(firstName.isEmpty() || lastName.isEmpty() || age.isEmpty() || email.isEmpty()){
            showMessage("Please fill all fields to remove", false)
            return
        }

        if(usersList.contains(
                User(firstName, lastName, age.toInt(), email))
            ){
            usersList.remove(User(firstName, lastName, age.toInt(), email))
            binding.tvUsersNumber.text = getString(R.string.users, usersList.size.toString())
            showMessage("User deleted successfully", true)
            clearEditTexts()
        }else{
            showMessage("User does not exists", false)
        }

    }

    private fun addUser(){
        val firstName = binding.etFirstname.text.toString()
        val lastName = binding.etLastname.text.toString()
        val age = binding.etAge.text.toString()
        val email = binding.etEmail.text.toString()

        if(firstName.isEmpty() || lastName.isEmpty() || age.isEmpty() || email.isEmpty()){
            showMessage("Please fill all fields", false)
            return
        }

        val checkUserExist = usersList.any{ it.email == email}
        if(checkUserExist){
            showMessage("User already exists(email is already taken)", false)
            return
        }

        val user = User(firstName, lastName, age.toInt(), email)
        usersList.add(user)
        binding.tvUsersNumber.text = getString(R.string.users, usersList.size.toString())
        showMessage("User added successfully", true)
        clearEditTexts()




    }
    private fun showMessage(message: String, isSuccess: Boolean){
        val color = if (isSuccess) {
            getColor(R.color.green)
        }else{
            getColor(R.color.red)
        }
        binding.tvSuccessError.text = message
        binding.tvSuccessError.setTextColor(color)
    }

    private fun clearEditTexts(){
        binding.etFirstname.text?.clear()
        binding.etLastname.text?.clear()
        binding.etAge.text?.clear()
        binding.etEmail.text?.clear()

    }

}