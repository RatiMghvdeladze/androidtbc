package com.example.androidtbc.domain.usecase

import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.model.ValidationResult
import com.example.androidtbc.domain.model.ValidationType
import com.example.androidtbc.domain.repository.AccountRepository
import com.example.androidtbc.domain.validators.AccountValidator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ValidateAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    operator fun invoke(accountNumber: String, validationType: ValidationType): Flow<Resource<ValidationResult>> = flow {
        emit(Resource.Loading(true))

        // Perform local validation based on type
        val isLocallyValid = when (validationType) {
            ValidationType.ACCOUNT_NUMBER -> AccountValidator.validateAccountNumber(accountNumber)
            ValidationType.PERSONAL_ID -> AccountValidator.validatePersonalId(accountNumber)
            ValidationType.PHONE_NUMBER -> AccountValidator.validatePhoneNumber(accountNumber)
        }

        if (!isLocallyValid) {
            emit(Resource.Error("Invalid format for the provided ${validationType.value}"))
            return@flow
        }

        // Local validation passed, now validate with repository
        // This is for ACCOUNT_NUMBER type only, as other types are just for demo
        if (validationType == ValidationType.ACCOUNT_NUMBER) {
            repository.validateAccount(accountNumber).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        emit(result)
                    }
                    is Resource.Error -> {
                        // For demo, we'll still consider it valid if local validation passed
                        emit(Resource.Success(ValidationResult("Success", true)))
                    }
                    is Resource.Loading -> {
                        // Pass through loading state
                        emit(result)
                    }
                }
            }
        } else {
            // For PERSONAL_ID and PHONE_NUMBER, just return success if local validation passed
            emit(Resource.Success(ValidationResult("Success", true)))
        }

        emit(Resource.Loading(false))
    }
}