package com.example.androidtbc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Address(
    val id: Int,
    val type: AddressType,
    val street: String,
    var isSelected: Boolean = false
) : Parcelable
