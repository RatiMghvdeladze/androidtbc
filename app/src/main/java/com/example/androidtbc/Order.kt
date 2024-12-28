package com.example.androidtbc

data class Order(
    val orderId: String,
    val trackingNumber: String,
    val quantity: Int,
    val subtotal: Int,
    val date: String,
    var orderStatus: StatusType
)
