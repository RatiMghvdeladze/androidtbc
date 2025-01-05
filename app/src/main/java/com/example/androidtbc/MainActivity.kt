package com.example.androidtbc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtbc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private var messageList = mutableListOf<Message>()
    private val chatListAdapter = ChatListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        addMessage(Message(messageText = "Hello! Can I help you?"))
        setUpRv()
        setUpClickListeners()
    }

    private fun setUpClickListeners() {
        binding.btnSend.setOnClickListener{
            val textMsg = binding.etMessage.text.toString().trim()
            binding.etMessage.text?.clear()
            addMessage(Message(messageText = textMsg))
        }

        binding.btnBack.setOnClickListener{
            finish()
        }
    }

    private fun setUpRv() {
        binding.rvMessages.adapter = chatListAdapter
        binding.rvMessages.layoutManager = LinearLayoutManager(this)
    }

    private fun addMessage(msg: Message){
        messageList.add(0, msg)
        chatListAdapter.submitList(messageList.toList())
        binding.rvMessages.scrollToPosition(0)
    }
}