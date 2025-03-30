package com.example.androidtbc.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    val id: Int,
    @SerialName("account_name")
    val accountName: String,
    @SerialName("account_number")
    val accountNumber: String,
    @SerialName("valute_type")
    val valuteType: String,
    @SerialName("card_type")
    val cardType: String,
    val balance: Double,
    @SerialName("card_logo")
    val cardLogo: String?
)
