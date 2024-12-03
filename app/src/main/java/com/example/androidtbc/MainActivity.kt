package com.example.androidtbc// com.example.androidtbc.MainActivity.kt
import NumberConverter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputNum: AppCompatEditText= findViewById(R.id.etNumber)
        val button: AppCompatButton = findViewById(R.id.btnConvert)
        val output: AppCompatTextView = findViewById(R.id.tvResult)

        val obj = NumberConverter()
        button.setOnClickListener{
            val s = inputNum.text.toString().toLong()
            output.text = obj.numberToText(s)
        }
    }
}