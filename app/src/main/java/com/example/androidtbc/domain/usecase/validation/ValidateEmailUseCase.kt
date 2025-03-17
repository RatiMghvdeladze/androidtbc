package com.example.androidtbc.domain.usecase.validation

import javax.inject.Inject

interface ValidateEmailUseCase {
    operator fun invoke(email: String): ValidationResult
}

class ValidateEmailUseCaseImpl @Inject constructor() : ValidateEmailUseCase {
    override operator fun invoke(email: String): ValidationResult {
        return if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error("Invalid email format")
        }
    }
}