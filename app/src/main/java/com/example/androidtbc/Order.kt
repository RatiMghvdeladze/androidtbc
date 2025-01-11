package com.example.androidtbc
import java.util.UUID

data class Order(
    val id: String = UUID.randomUUID().toString(),
    val color: String,
    val title: String,
    var image: Int,
    val quantity: Int,
    var price: Double,
    val status: OrderType,
)