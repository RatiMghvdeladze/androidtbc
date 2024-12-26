package com.example.androidtbc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class AddressType : Parcelable{
    Home,
    Office
}
