package com.example.androidtbc.domain.usecase.validation

import javax.inject.Inject

interface ValidatePasswordUseCase {
    operator fun invoke(password: String): ValidationResult
}

class ValidatePasswordUseCaseImpl @Inject constructor() : ValidatePasswordUseCase {
    override operator fun invoke(password: String): ValidationResult {
        return when {
            password.isEmpty() -> ValidationResult.Error("Password cannot be empty")
            password.length < 6 -> ValidationResult.Error("Password must be at least 6 characters")
            !password.any { it.isDigit() } -> ValidationResult.Error("Password must contain at least one digit")
            !password.any { it.isUpperCase() } -> ValidationResult.Error("Password must contain at least one uppercase letter")
            else -> ValidationResult.Success
        }
    }
}