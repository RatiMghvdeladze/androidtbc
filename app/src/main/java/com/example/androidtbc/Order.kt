package com.example.androidtbc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val orderId: String,
    val trackingNumber: String,
    val quantity: Int,
    val subtotal: Int,
    val dateInMillis: Long,
    var orderStatus: StatusType
) : Parcelable
