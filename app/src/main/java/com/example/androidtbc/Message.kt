package com.example.androidtbc

import java.util.UUID

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val messageText: String,
    val time: Long = System.currentTimeMillis()
    )