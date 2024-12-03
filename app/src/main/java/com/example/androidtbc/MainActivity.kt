package com.example.androidtbc
import NumberConverter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.AppCompatToggleButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputNum: AppCompatEditText = findViewById(R.id.etNumber)
        val button: AppCompatButton = findViewById(R.id.btnConvert)
        val output: AppCompatTextView = findViewById(R.id.tvResult)
        val toggle: AppCompatToggleButton = findViewById(R.id.tglLanguage)



        toggle.setOnClickListener {
            if (toggle.isChecked) {
                button.text = getString(R.string.convertEN)
            } else {
                button.text = getString(R.string.convert)
            }
        }

        val ge = NumberConverter()
        val en = NumberConverterEN()

        button.setOnClickListener {

            val txt = inputNum.text.toString().toLongOrNull()
            if(txt != null) {
                if (!toggle.isChecked) {
                    output.text = ge.numberToText(txt)
                } else {
                    output.text = en.numberToTextEN(txt)
                }
            }else{
                if(!toggle.isChecked()) {
                    output.text = getString(R.string.error_message_null_ge)
                }else{
                    output.text = getString(R.string.error_message_null_en)
                }
            }
        }
    }
}