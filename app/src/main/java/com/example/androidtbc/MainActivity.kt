package com.example.androidtbc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtbc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, SetupFragment())
            .commit()
    }
}
