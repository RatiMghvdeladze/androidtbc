package com.example.androidtbc

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtbc.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var inputValidator: InputValidator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth = FirebaseAuth.getInstance()
        inputValidator = InputValidator()

        binding.btnBack.setOnClickListener { getBack() }

        binding.btnNext.setOnClickListener { onNextClick() }
    }

    private fun onNextClick() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (!inputValidator.isEmailValid(email)) {
            binding.etEmail.error = "Invalid email format"
            return
        }
        if (!inputValidator.isValidPassword(password)) {
            binding.etPassword.error = "Invalid password format"
            return
        }

        val intent = Intent(this, RegisterSecondActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("password", password)
        startActivity(intent)
    }

    private fun getBack() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }
}
