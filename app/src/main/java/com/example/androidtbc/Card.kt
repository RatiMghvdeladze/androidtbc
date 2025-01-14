package com.example.androidtbc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Card(
    val id: String = UUID.randomUUID().toString(),
    val cardNumber: String,
    val name: String,
    val validThru: String,
    val type: CardType,
    val cvv: String,
): Parcelable
