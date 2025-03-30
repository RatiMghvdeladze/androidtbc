package com.example.androidtbc.presentation.utils

import com.example.androidtbc.R
import com.example.androidtbc.presentation.model.CardTypeUI

object CardUtils {
    fun getCardLogoResource(cardTypeUI: CardTypeUI): Int = when(cardTypeUI) {
        CardTypeUI.VISA -> R.drawable.ic_visa
        CardTypeUI.MASTER_CARD -> R.drawable.ic_mastercard
    }
}