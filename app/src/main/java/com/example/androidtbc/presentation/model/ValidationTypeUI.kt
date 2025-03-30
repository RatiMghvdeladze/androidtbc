package com.example.androidtbc.presentation.model

import com.example.androidtbc.domain.model.ValidationType as DomainValidationType

enum class ValidationTypeUI {
    ACCOUNT_NUMBER,
    PERSONAL_ID,
    PHONE_NUMBER;

    fun toDomain(): DomainValidationType = when(this) {
        ACCOUNT_NUMBER -> DomainValidationType.ACCOUNT_NUMBER
        PERSONAL_ID -> DomainValidationType.PERSONAL_ID
        PHONE_NUMBER -> DomainValidationType.PHONE_NUMBER
    }
}