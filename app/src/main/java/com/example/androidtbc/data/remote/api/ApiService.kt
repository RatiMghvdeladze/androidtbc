package com.example.androidtbc.data.remote.api

import com.example.androidtbc.data.remote.model.AccountDto
import com.example.androidtbc.data.remote.model.ExchangeRateResponseDto
import com.example.androidtbc.data.remote.model.TransferResponseDto
import com.example.androidtbc.data.remote.model.ValidationResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("v3/d689fe3e-6faf-446a-9896-c538de3449fa")
    suspend fun getAccounts(): Response<List<AccountDto>>

    @GET("v3/29d002d4-3ccd-4eaa-95eb-a9d1601ce123")
    suspend fun validateAccount(
        @Query("account_number") accountNumber: String
    ): Response<ValidationResponseDto>

    @GET("v3/d9eab148-a083-4625-9f9a-9ada0d409ba3")
    suspend fun getExchangeRate(
        @Query("from_account") fromAccount: String,
        @Query("to_account") toAccount: String
    ): Response<ExchangeRateResponseDto>

    @GET("v3/a78769e5-98ef-4a56-a3d4-ed7683447806")
    suspend fun transferMoney(
        @Query("from_account") fromAccount: String,
        @Query("to_account") toAccount: String,
        @Query("money") amount: Double
    ): Response<TransferResponseDto>
}