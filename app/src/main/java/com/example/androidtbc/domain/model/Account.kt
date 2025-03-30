package com.example.androidtbc.domain.model

data class Account(
    val id: Int,
    val accountName: String,
    val accountNumber: String,
    val valuteType: CurrencyType,
    val cardType: CardType,
    val balance: Double,
    val cardLogo: String?
)