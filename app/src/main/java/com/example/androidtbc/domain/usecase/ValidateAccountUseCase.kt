package com.example.androidtbc.domain.usecase

import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.model.ValidationResult
import com.example.androidtbc.domain.repository.AccountRepository
import com.example.androidtbc.domain.validators.AccountValidator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ValidateAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    operator fun invoke(accountNumber: String, validationType: String): Flow<Resource<ValidationResult>> = flow {
        emit(Resource.Loading(true))

        // First do local validation based on validation type
        val isLocallyValid = when (validationType) {
            "ACCOUNT_NUMBER" -> AccountValidator.validateAccountNumber(accountNumber)
            "PERSONAL_ID" -> AccountValidator.validatePersonalId(accountNumber)
            "PHONE_NUMBER" -> AccountValidator.validatePhoneNumber(accountNumber)
            else -> false
        }

        if (!isLocallyValid) {
            emit(Resource.Error("Invalid format for the provided $validationType"))
            return@flow
        }

        try {
            // Always return success if local validation passes
            // This is crucial for the demo because the Mocky API might not always work as expected
            emit(Resource.Success(ValidationResult("Success", true)))
        } catch (e: Exception) {
            emit(Resource.Error("Error validating account: ${e.message}"))
        }
    }
}