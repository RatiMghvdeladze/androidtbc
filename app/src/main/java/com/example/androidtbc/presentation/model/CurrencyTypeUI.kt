package com.example.androidtbc.presentation.model

import com.example.androidtbc.domain.model.CardType as DomainCardType
import com.example.androidtbc.domain.model.CurrencyType as DomainCurrencyType

enum class CurrencyTypeUI {
    USD,
    GEL,
    EUR;

    fun toDomain(): DomainCurrencyType = when(this) {
        USD -> DomainCurrencyType.USD
        GEL -> DomainCurrencyType.GEL
        EUR -> DomainCurrencyType.EUR
    }

    companion object {
        fun fromDomain(domainCurrency: DomainCurrencyType): CurrencyTypeUI = when(domainCurrency) {
            DomainCurrencyType.USD -> USD
            DomainCurrencyType.GEL -> GEL
            DomainCurrencyType.EUR -> EUR
        }
    }
}

enum class CardTypeUI {
    VISA,
    MASTER_CARD;

    companion object {
        fun fromDomain(domainCard: DomainCardType): CardTypeUI = when(domainCard) {
            DomainCardType.VISA -> VISA
            DomainCardType.MASTER_CARD -> MASTER_CARD
        }
    }
}