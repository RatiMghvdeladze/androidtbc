package com.example.androidtbc.domain.usecase.validation

sealed class ValidationResult {
    data object Success: ValidationResult()
    data class Error(val errorMessage: String): ValidationResult()
}