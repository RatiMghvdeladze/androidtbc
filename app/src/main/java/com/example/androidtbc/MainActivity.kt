package com.example.androidtbc

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtbc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val wordsList = mutableListOf<String>()

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

        binding.btnSave.setOnClickListener {
            onSaveClick()
        }

        binding.btnOutput.setOnClickListener {
            onOutputClick()
        }

        binding.btnClear.setOnClickListener {
            clearAllDisplays()
        }

    }

    private fun clearAllDisplays() {
        binding.tvResult.clear()
        binding.tvAnagram.clear()
        binding.tvOutput.clear()
        binding.etAnagramEnter.clear()
        wordsList.clear()
        Toast.makeText(binding.root.context, "everything is cleared", Toast.LENGTH_SHORT).show()
    }

    private fun onSaveClick() {
        val word = binding.etAnagramEnter.text.toString().trim()
        if (word.isNotEmpty()) {
            binding.tvOutput.visibility = View.GONE
            binding.tvResult.visibility = View.GONE
            wordsList.add(word)
            binding.tvAnagram.text =
                getString(R.string.saved_words_are_, wordsList.joinToString(","))
            binding.etAnagramEnter.text?.clear()
        }
    }

    private fun onOutputClick() {
        val anagrams = groupAnagrams(wordsList)
        if(wordsList.isEmpty()){
            binding.tvOutput.text =
                getString(R.string.what_should_i_display_there_is_nothing_stored)
            binding.tvOutput.visibility = View.VISIBLE
        }
        val displayText = anagrams.joinToString(", ") {
            it.joinToString(", ", "[", "]")
        }
        binding.tvResult.text = displayText
        binding.tvResult.visibility = View.VISIBLE
    }

    private fun groupAnagrams(wordsList: List<String>): List<List<String>> {
        return wordsList.groupBy { it.toCharArray().sorted().joinToString() }.values.toList()
    }


}