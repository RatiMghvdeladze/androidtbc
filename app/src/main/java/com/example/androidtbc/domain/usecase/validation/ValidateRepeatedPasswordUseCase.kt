package com.example.androidtbc.domain.usecase.validation

import com.example.androidtbc.domain.validation.ValidationResult
import javax.inject.Inject

class ValidateRepeatedPasswordUseCase @Inject constructor() {
    operator fun invoke(password: String, repeatedPassword: String): ValidationResult {
        return if (password == repeatedPassword) {
            ValidationResult.Success
        } else {
            ValidationResult.Error("Passwords do not  match")
        }
    }
}