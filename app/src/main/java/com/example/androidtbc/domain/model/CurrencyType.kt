package com.example.androidtbc.domain.model

enum class CurrencyType(val value: String) {
    USD("USD"),
    GEL("GEL"),
    EUR("EUR");

    companion object {
        fun fromString(value: String): CurrencyType = entries.find { it.value == value } ?: GEL
    }
}

enum class CardType(val value: String) {
    VISA("VISA"),
    MASTER_CARD("MASTER_CARD");

    companion object {
        fun fromString(value: String): CardType = entries.find { it.value == value } ?: VISA
    }
}