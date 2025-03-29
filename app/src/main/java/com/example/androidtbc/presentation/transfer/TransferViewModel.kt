package com.example.androidtbc.presentation.transfer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtbc.domain.common.Resource
import com.example.androidtbc.domain.manager.AccountManager
import com.example.androidtbc.domain.usecase.GetAccountsUseCase
import com.example.androidtbc.domain.usecase.GetExchangeRateUseCase
import com.example.androidtbc.domain.usecase.TransferMoneyUseCase
import com.example.androidtbc.domain.usecase.ValidateAccountUseCase
import com.example.androidtbc.presentation.model.AccountUI
import com.example.androidtbc.presentation.model.ExchangeRateUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val validateAccountUseCase: ValidateAccountUseCase,
    private val getExchangeRateUseCase: GetExchangeRateUseCase,
    private val transferMoneyUseCase: TransferMoneyUseCase,
    private val accountManager: AccountManager
) : ViewModel() {

    private val _state = MutableStateFlow(TransferState())
    val state: StateFlow<TransferState> = _state.asStateFlow()

    private val _effect = Channel<TransferEffect>()
    val effect = _effect.receiveAsFlow()

    // Flag to prevent feedback loops in bidirectional updates
    private var isUpdatingAmount = false

    // Coroutine dispatchers for different types of work
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    // Error handler to prevent app crashes from coroutines
    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch(Dispatchers.Main) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = exception.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    // In TransferViewModel
    init {
        Log.d("TransferViewModel", "Initializing ViewModel and loading accounts")
        loadAccounts()
    }

    fun onEvent(event: TransferEvent) {
        when (event) {
            is TransferEvent.LoadAccounts -> loadAccounts()
            is TransferEvent.SelectFromAccount -> selectFromAccount(event.account)
            is TransferEvent.SelectToAccount -> selectToAccount(event.account)
            is TransferEvent.ValidateAccount -> validateAccount(event.accountNumber, event.validationType)
            is TransferEvent.UpdateSellAmount -> updateSellAmount(event.amount)
            is TransferEvent.UpdateReceiveAmount -> updateReceiveAmount(event.amount)
            is TransferEvent.UpdateDescription -> updateDescription(event.description)
            is TransferEvent.Transfer -> transfer(event.fromAccount, event.toAccount, event.amount)
            is TransferEvent.ClearError -> clearError()
        }
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            Log.d("TransferViewModel", "Starting to load accounts")
            _state.update { it.copy(isLoading = true) }

            getAccountsUseCase()
                .catch { exception ->
                    Log.e("TransferViewModel", "Error loading accounts: ${exception.message}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Unknown error occurred"
                        )
                    }
                }
                .collectLatest { result ->
                    when (result) {
                        is Resource.Success -> {
                            Log.d("TransferViewModel", "Successfully loaded ${result.data.size} accounts")
                            val accountsUI = result.data.map { AccountUI.fromDomain(it) }

                            // Store current selected accounts to preserve references
                            val currentFromAccount = _state.value.fromAccount
                            val currentToAccount = _state.value.toAccount

                            // Update accounts list
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    accounts = accountsUI,
                                    error = null
                                )
                            }

                            // Now update selected accounts with refreshed data if they exist
                            if (currentFromAccount != null || currentToAccount != null) {
                                updateSelectedAccounts()
                            }
                        }
                        is Resource.Error -> {
                            Log.e("TransferViewModel", "Error loading accounts: ${result.errorMessage}")
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.errorMessage
                                )
                            }
                        }
                        is Resource.Loading -> {
                            _state.update { it.copy(isLoading = result.isLoading) }
                        }
                    }
                }
        }
    }

    private fun selectFromAccount(account: AccountUI) {
        viewModelScope.launch(Dispatchers.Main) {
            _state.update {
                it.copy(
                    fromAccount = account,
                    error = null
                )
            }

            // If to account is also selected, check if currencies match
            _state.value.toAccount?.let { toAccount ->
                checkCurrencies(account, toAccount)
            }
        }
    }

    private fun selectToAccount(account: AccountUI) {
        viewModelScope.launch(Dispatchers.Main) {
            _state.update {
                it.copy(
                    toAccount = account,
                    error = null
                )
            }

            // If from account is also selected, check if currencies match
            _state.value.fromAccount?.let { fromAccount ->
                checkCurrencies(fromAccount, account)
            }
        }
    }

    private fun checkCurrencies(fromAccount: AccountUI, toAccount: AccountUI) {
        viewModelScope.launch(Dispatchers.Main) {
            if (fromAccount.valuteType == toAccount.valuteType) {
                _state.update {
                    it.copy(
                        showSameCurrencyInput = true,
                        showDifferentCurrencyInputs = false,
                        exchangeRate = null
                    )
                }

                // Ensure receive amount matches sell amount for same currency
                updateSellAmount(_state.value.sellAmount)
            } else {
                _state.update {
                    it.copy(
                        showSameCurrencyInput = false,
                        showDifferentCurrencyInputs = true
                    )
                }
                fetchExchangeRate(fromAccount.valuteType, toAccount.valuteType)
            }
        }
    }

    private fun validateAccount(accountNumber: String, validationType: String) {
        viewModelScope.launch(errorHandler + ioDispatcher) {
            withContext(Dispatchers.Main) {
                _state.update { it.copy(isLoading = true) }
            }

            try {
                validateAccountUseCase(accountNumber, validationType)
                    .catch { exception ->
                        withContext(Dispatchers.Main) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = exception.message ?: "Validation failed"
                                )
                            }
                        }
                    }
                    .flowOn(ioDispatcher)
                    .collectLatest { result ->
                        withContext(Dispatchers.Main) {
                            when (result) {
                                is Resource.Success -> {
                                    _state.update {
                                        it.copy(
                                            isLoading = false,
                                            accountValidation = result.data,
                                            error = null
                                        )
                                    }

                                    if (result.data.isValid) {
                                        // For account numbers, try to find in our list first
                                        if (validationType == "ACCOUNT_NUMBER") {
                                            val matchingAccountUI = _state.value.accounts.find {
                                                it.accountNumber.replace(" ", "") == accountNumber.replace(" ", "")
                                            }

                                            if (matchingAccountUI != null) {
                                                selectToAccount(matchingAccountUI)
                                            } else {
                                                // Create a temporary account for any valid input
                                                createTemporaryToAccount(accountNumber, validationType)
                                            }
                                        } else {
                                            // For personal ID or phone numbers, always create a temporary account
                                            createTemporaryToAccount(accountNumber, validationType)
                                        }
                                    } else {
                                        _state.update { it.copy(error = "Invalid account") }
                                    }
                                }
                                is Resource.Error -> {
                                    _state.update {
                                        it.copy(
                                            isLoading = false,
                                            error = result.errorMessage
                                        )
                                    }
                                }
                                is Resource.Loading -> {
                                    _state.update { it.copy(isLoading = result.isLoading) }
                                }
                            }
                        }
                    }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Validation failed"
                        )
                    }
                }
            }
        }
    }

    private fun createTemporaryToAccount(identifier: String, type: String) {
        viewModelScope.launch(defaultDispatcher) {
            // Create a display-friendly name based on the type (can be intensive for large identifiers)
            val name = when (type) {
                "ACCOUNT_NUMBER" -> "External Account"
                "PERSONAL_ID" -> "ID: ${identifier.take(4)}****${identifier.takeLast(3)}"
                "PHONE_NUMBER" -> "Phone: ${identifier.take(3)}****${identifier.takeLast(2)}"
                else -> "External Account"
            }

            val tempAccountUI = AccountUI(
                id = -1, // Temporary ID
                accountName = name,
                accountNumber = identifier,
                valuteType = _state.value.fromAccount?.valuteType ?: "GEL", // Default to same currency
                cardType = "VISA", // Default
                balance = 0.0, // Unknown balance
                cardLogo = null,
                maskedNumber = if (identifier.length > 4) "**** ${identifier.takeLast(4)}" else identifier
            )

            withContext(Dispatchers.Main) {
                selectToAccount(tempAccountUI)
            }
        }
    }

    private fun fetchExchangeRate(fromCurrency: String, toCurrency: String) {
        viewModelScope.launch(errorHandler + ioDispatcher) {
            withContext(Dispatchers.Main) {
                _state.update { it.copy(isLoading = true) }
            }

            try {
                getExchangeRateUseCase(fromCurrency, toCurrency)
                    .catch { exception ->
                        Log.e("TransferViewModel", "Error fetching exchange rate: ${exception.message}")
                        // Even on exception, we'll use our fallback rate
                        withContext(Dispatchers.Main) {
                            _state.update { it.copy(isLoading = false) }
                        }
                    }
                    .flowOn(ioDispatcher)
                    .collectLatest { result ->
                        withContext(Dispatchers.Main) {
                            when (result) {
                                is Resource.Success -> {
                                    val exchangeRateUI = withContext(defaultDispatcher) {
                                        ExchangeRateUI.fromDomain(result.data)
                                    }

                                    Log.d("TransferViewModel", "Exchange rate fetched: ${result.data.rate} for ${result.data.fromCurrency} to ${result.data.toCurrency}")

                                    _state.update {
                                        it.copy(
                                            isLoading = false,
                                            exchangeRate = exchangeRateUI,
                                            error = null
                                        )
                                    }

                                    // When we get a new exchange rate, update the receive amount based on current sell amount
                                    updateSellAmount(_state.value.sellAmount)
                                }
                                is Resource.Error -> {
                                    Log.e("TransferViewModel", "Error in exchange rate result: ${result.errorMessage}")
                                    // Error is already handled in the use case by providing fallback rates
                                    _state.update { it.copy(isLoading = false) }
                                }
                                is Resource.Loading -> {
                                    _state.update { it.copy(isLoading = result.isLoading) }
                                }
                            }
                        }
                    }
            } catch (e: Exception) {
                Log.e("TransferViewModel", "Exception in fetchExchangeRate: ${e.message}")
                withContext(Dispatchers.Main) {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    // Fixed: These operations now properly calculate and update amounts
    private fun updateSellAmount(amount: Double) {
        // If already updating, don't start another update to prevent feedback loop
        if (isUpdatingAmount) return

        viewModelScope.launch(Dispatchers.Main) {
            try {
                isUpdatingAmount = true

                // Update the sell amount
                _state.update { it.copy(sellAmount = amount) }
                Log.d("TransferViewModel", "Setting sell amount: $amount")

                // Calculate receive amount if exchange rate is available
                _state.value.exchangeRate?.let { rate ->
                    // Calculate with rounding to prevent floating point issues
                    val receiveAmount = round(amount * rate.rate)
                    Log.d("TransferViewModel", "Calculated receive amount: $receiveAmount from sell amount: $amount with rate: ${rate.rate}")

                    // Only update if the change is significant
                    if (abs(_state.value.receiveAmount - receiveAmount) > 0.001) {
                        _state.update { it.copy(receiveAmount = receiveAmount) }
                    }
                } ?: run {
                    // If same currency or rate not yet available
                    _state.update { it.copy(receiveAmount = amount) }
                }
            } finally {
                isUpdatingAmount = false
            }
        }
    }

    private fun updateReceiveAmount(amount: Double) {
        // If already updating, don't start another update to prevent feedback loop
        if (isUpdatingAmount) return

        viewModelScope.launch(Dispatchers.Main) {
            try {
                isUpdatingAmount = true

                // Update the receive amount
                _state.update { it.copy(receiveAmount = amount) }
                Log.d("TransferViewModel", "Setting receive amount: $amount")

                // Calculate sell amount if exchange rate is available
                _state.value.exchangeRate?.let { rate ->
                    if (rate.rate > 0) {
                        // Calculate with rounding to prevent floating point issues
                        val sellAmount = round(amount / rate.rate)
                        Log.d("TransferViewModel", "Calculated sell amount: $sellAmount from receive amount: $amount with rate: ${rate.rate}")

                        // Only update if the change is significant
                        if (abs(_state.value.sellAmount - sellAmount) > 0.001) {
                            _state.update { it.copy(sellAmount = sellAmount) }
                        }
                    } else {
                        Log.e("TransferViewModel", "Invalid exchange rate: ${rate.rate}")
                        _state.update { it.copy(error = "Invalid exchange rate") }
                    }
                } ?: run {
                    // If same currency or rate not yet available
                    _state.update { it.copy(sellAmount = amount) }
                }
            } finally {
                isUpdatingAmount = false
            }
        }
    }

    // Helper function to round to 2 decimal places
    private fun round(value: Double): Double {
        return Math.round(value * 100) / 100.0
    }

    private fun updateDescription(description: String) {
        _state.update { it.copy(description = description) }
    }

    // Inside TransferViewModel class
    /**
     * Processes a money transfer between accounts
     * Uses TransferMoneyUseCase to update account balances and verify the transfer
     */
    /**
     * Processes a money transfer between accounts
     * Simplified version that focuses on the essential transfer logic
     */
    private fun transfer(fromAccount: String, toAccount: String, amount: Double) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Validate source account and funds
            val sourceAccount = accountManager.getAccount(fromAccount)
            when {
                sourceAccount == null -> {
                    _state.update { it.copy(isLoading = false, error = "Source account not found") }
                    return@launch
                }
                sourceAccount.balance < amount -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(TransferEffect.ShowInsufficientFundsError)
                    return@launch
                }
            }

            try {
                // Process transfer using use case
                var success = false

                transferMoneyUseCase(fromAccount, toAccount, amount).collect { result ->
                    when (result) {
                        is Resource.Success -> success = result.data.isSuccessful
                        is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.errorMessage) }
                        is Resource.Loading -> _state.update { it.copy(isLoading = result.isLoading) }
                    }
                }

                if (!success) {
                    _state.update { it.copy(isLoading = false, error = "Failed to transfer money") }
                    return@launch
                }

                // On success: reload accounts to get updated balances
                loadAccounts()

                // Important: Wait a moment to ensure accounts are loaded before updating UI state
                delay(100)

                // Update any selected accounts with their new balances
                val updatedAccounts = _state.value.accounts
                val updatedFromAccount = updatedAccounts.find { it.accountNumber == fromAccount }
                val updatedToAccount = updatedAccounts.find { it.accountNumber == toAccount }

                _state.update {
                    it.copy(
                        isLoading = false,
                        transferSuccess = true,
                        error = null,
                        fromAccount = updatedFromAccount ?: it.fromAccount,
                        toAccount = updatedToAccount ?: it.toAccount
                    )
                }

                _effect.send(TransferEffect.NavigateToSuccess("Transfer completed successfully"))

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Transfer failed") }
            }
        }
    }
    /**
     * Updates the selected accounts with their current values from the accounts list
     * This ensures the UI displays the most current balances
     */
    private fun updateSelectedAccounts() {
        viewModelScope.launch(Dispatchers.Main) {
            val accounts = _state.value.accounts
            val currentFromAccount = _state.value.fromAccount
            val currentToAccount = _state.value.toAccount

            // Only proceed if we have accounts and at least one selected account
            if (accounts.isEmpty() || (currentFromAccount == null && currentToAccount == null)) {
                return@launch
            }

            // Find updated accounts by matching account numbers
            val updatedFromAccount = currentFromAccount?.let { selected ->
                accounts.find { it.accountNumber == selected.accountNumber }
            }

            val updatedToAccount = currentToAccount?.let { selected ->
                accounts.find { it.accountNumber == selected.accountNumber }
            }

            // Update state with refreshed account data
            _state.update { state ->
                state.copy(
                    fromAccount = updatedFromAccount ?: state.fromAccount,
                    toAccount = updatedToAccount ?: state.toAccount
                )
            }

            Log.d("TransferViewModel", "Updated selected accounts: From=${updatedFromAccount?.balance}, To=${updatedToAccount?.balance}")
        }
    }
    private fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun showFromAccountBottomSheet() {
        viewModelScope.launch(Dispatchers.Main) {
            _effect.send(TransferEffect.ShowFromAccountBottomSheet)
        }
    }
}