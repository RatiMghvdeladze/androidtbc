package com.example.androidtbc

import java.util.UUID

data class Card(
    val id: String = UUID.randomUUID().toString(),
    val cardNumber: String,
    val name: String,
    val validThru: String,
    val type: CardType,
    val cvv: String,
)
