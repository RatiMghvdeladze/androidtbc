package com.example.androidtbc

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private var savedFieldValues: Array<String>? = null


    private lateinit var btnSave: AppCompatButton
    private lateinit var btnClear: AppCompatButton
    private lateinit var btnAgain: AppCompatButton

    private lateinit var etEmail: AppCompatEditText
    private lateinit var etUsername: AppCompatEditText
    private lateinit var etFirstName: AppCompatEditText
    private lateinit var etLastName: AppCompatEditText
    private lateinit var etAge: AppCompatEditText
    private lateinit var tvOutput: AppCompatTextView

    private lateinit var tvEmail: AppCompatTextView
    private lateinit var tvUsername: AppCompatTextView
    private lateinit var tvFullName: AppCompatTextView
    private lateinit var tvAge: AppCompatTextView

    private lateinit var tvContainer: LinearLayoutCompat
    private lateinit var etContainer: LinearLayoutCompat

    private val inputValidator: InputValidator = InputValidator()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()


        btnSave.setOnClickListener {
            onSaveClick()


        }

        btnClear.setOnLongClickListener {
            clearAllFields()
            true
        }

        btnAgain.setOnClickListener {
            clearAllFields()
            btnSave.visibility = View.VISIBLE
            btnClear.visibility = View.VISIBLE
            etContainer.visibility = View.VISIBLE
            tvContainer.visibility = View.GONE
        }

    }


    private fun clearAllFields() {
        val arr = arrayOf(
            etEmail, etUsername,
            etFirstName, etLastName,
            etAge
        )
        arr.forEach { it.text?.clear() }
        tvOutput.clear()
    }

    private fun validateInputs(): Boolean {
        val emailTxt = etEmail.text.toString()
        val usernameTxt = etUsername.text.toString()
        val firstnameTxt = etFirstName.text.toString()
        val lastnameTxt = etLastName.text.toString()
        val ageTxt = etAge.text.toString()

        val arrEtText = arrayOf(emailTxt, usernameTxt, firstnameTxt, lastnameTxt, ageTxt)

        var message = ""
        var isValid = true

        if (!inputValidator.isEverythingFill(arrEtText)) {
            message += "${getString(R.string.fill_all_fields)}\n"
            isValid = false
        }
        if (!inputValidator.isEmailValid(emailTxt)) {
            message += "${getString(R.string.email_invalid_format)}\n"
            isValid = false
        }
        if (!inputValidator.isUsernameValid(usernameTxt)) {
            message += "${getString(R.string.username_invalid_format)}\n"
            isValid = false
        }

        if (!inputValidator.isAgeValid(ageTxt)) {
            message += "${getString(R.string.email_invalid_format)}\n"
            isValid = false
        }

        tvOutput.text = message
        return isValid
    }

    private fun onSaveClick() {
        val isValid = validateInputs()
        if (isValid) {
            etContainer.visibility = View.GONE
            tvContainer.visibility = View.VISIBLE

            tvEmail.text = getString(R.string.display_email, etEmail.text.toString())
            tvUsername.text = getString(R.string.display_username, etUsername.text.toString())
            tvFullName.text = getString(
                R.string.display_full_name,
                etFirstName.text.toString(),
                etLastName.text.toString()
            )
            tvAge.text = getString(R.string.display_age, etAge.text.toString())

            btnSave.visibility = View.GONE
            btnClear.visibility = View.GONE
        }


    }


    private fun initViews() {
        btnSave = findViewById(R.id.btnSave)
        btnClear = findViewById(R.id.btnClear)
        btnAgain = findViewById(R.id.btnAgain)

        etEmail = findViewById(R.id.etEmail)
        etUsername = findViewById(R.id.etUsername)
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etAge = findViewById(R.id.etAge)
        etContainer = findViewById(R.id.etContainer)


        tvOutput = findViewById(R.id.tvOutput)


        tvEmail = findViewById(R.id.tvEmail)
        tvUsername = findViewById(R.id.tvUsername)
        tvFullName = findViewById(R.id.tvFullName)
        tvAge = findViewById(R.id.tvAge)
        tvContainer = findViewById(R.id.tvContainer)


    }


}