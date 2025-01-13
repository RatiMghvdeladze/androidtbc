package com.example.androidtbc

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.androidtbc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var cardAdapter: CardAdapter

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
        initVP()

    }

    private fun initVP() {
        cardAdapter = CardAdapter()
        binding.vp2.apply {
            adapter = cardAdapter
        }

        val cards = listOf(
            Card(cardNumber = "1234 5678 9101 1234", name = "rati", validThru = "05/26", type = CardType.VISA),
            Card(cardNumber = "1234 5678 9101 9192", name = "OKEII", validThru = "05/26", type = CardType.MASTERCARD),
            Card(cardNumber = "1234 5678 9101 9192", name = "OKEII", validThru = "05/26", type = CardType.MASTERCARD),
            Card(cardNumber = "1234 5678 9101 9192", name = "OKEII", validThru = "05/26", type = CardType.MASTERCARD),
            Card(cardNumber = "1234 5678 9101 9192", name = "OKEII", validThru = "05/26", type = CardType.MASTERCARD),
            Card(cardNumber = "1234 5678 9101 9192", name = "OKEII", validThru = "05/26", type = CardType.MASTERCARD),
        )

        cardAdapter.submitList(cards.toList())

    }
}