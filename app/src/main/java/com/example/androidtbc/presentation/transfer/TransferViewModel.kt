package com.example.androidtbc.presentation.transfer

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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    init {
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
            is TransferEvent.UpdateDescription -> _state.update { it.copy(description = event.description) }
            is TransferEvent.Transfer -> transfer(event.fromAccount, event.toAccount, event.amount)
            is TransferEvent.ClearError -> _state.update { it.copy(error = null) }
            is TransferEvent.ClearInputs -> resetInputValues()
        }
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getAccountsUseCase()
                .catch { exception ->
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
                            val accountsUI = result.data.map { AccountUI.fromDomain(it) }
                            val currentFromAccount = _state.value.fromAccount
                            val currentToAccount = _state.value.toAccount

                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    accounts = accountsUI,
                                    error = null
                                )
                            }

                            if (currentFromAccount != null || currentToAccount != null) {
                                updateSelectedAccounts()
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
    }

    private fun selectFromAccount(account: AccountUI) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    fromAccount = account,
                    error = null
                )
            }

            _state.value.toAccount?.let { toAccount ->
                checkCurrencies(account, toAccount)
            }
        }
    }

    private fun selectToAccount(account: AccountUI) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    toAccount = account,
                    error = null
                )
            }

            _state.value.fromAccount?.let { fromAccount ->
                checkCurrencies(fromAccount, account)
            }
        }
    }

    private fun checkCurrencies(fromAccount: AccountUI, toAccount: AccountUI) {
        viewModelScope.launch {
            if (fromAccount.valuteType == toAccount.valuteType) {
                _state.update {
                    it.copy(
                        showSameCurrencyInput = true,
                        showDifferentCurrencyInputs = false,
                        exchangeRate = null
                    )
                }
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
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            validateAccountUseCase(accountNumber, validationType)
                .catch { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Validation failed"
                        )
                    }
                }
                .collectLatest { result ->
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
                                if (validationType == "ACCOUNT_NUMBER") {
                                    val matchingAccountUI = _state.value.accounts.find {
                                        it.accountNumber.replace(" ", "") == accountNumber.replace(" ", "")
                                    }

                                    if (matchingAccountUI != null) {
                                        selectToAccount(matchingAccountUI)
                                    } else {
                                        createTemporaryToAccount(accountNumber, validationType)
                                    }
                                } else {
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
    }

    private fun createTemporaryToAccount(identifier: String, type: String) {
        val name = when (type) {
            "ACCOUNT_NUMBER" -> "External Account"
            "PERSONAL_ID" -> "ID: ${identifier.take(4)}****${identifier.takeLast(3)}"
            "PHONE_NUMBER" -> "Phone: ${identifier.take(3)}****${identifier.takeLast(2)}"
            else -> "External Account"
        }

        val tempAccountUI = AccountUI(
            id = -1,
            accountName = name,
            accountNumber = identifier,
            valuteType = _state.value.fromAccount?.valuteType ?: "GEL",
            cardType = "VISA",
            balance = 0.0,
            cardLogo = null,
            maskedNumber = if (identifier.length > 4) "**** ${identifier.takeLast(4)}" else identifier
        )

        viewModelScope.launch {
            selectToAccount(tempAccountUI)
        }
    }

    private fun fetchExchangeRate(fromCurrency: String, toCurrency: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getExchangeRateUseCase(fromCurrency, toCurrency)
                .catch {
                    _state.update { it.copy(isLoading = false) }
                }
                .collectLatest { result ->
                    when (result) {
                        is Resource.Success -> {
                            val exchangeRateUI = ExchangeRateUI.fromDomain(result.data)

                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    exchangeRate = exchangeRateUI,
                                    error = null
                                )
                            }
                            updateSellAmount(_state.value.sellAmount)
                        }
                        is Resource.Error -> {
                            _state.update { it.copy(isLoading = false) }
                        }
                        is Resource.Loading -> {
                            _state.update { it.copy(isLoading = result.isLoading) }
                        }
                    }
                }
        }
    }

    private fun updateSellAmount(amount: Double) {
        if (isUpdatingAmount) return

        viewModelScope.launch {
            isUpdatingAmount = true

            _state.update { it.copy(sellAmount = amount) }

            _state.value.exchangeRate?.let { rate ->
                val receiveAmount = round(amount * rate.rate)

                if (abs(_state.value.receiveAmount - receiveAmount) > 0.001) {
                    _state.update { it.copy(receiveAmount = receiveAmount) }
                }
            } ?: run {
                _state.update { it.copy(receiveAmount = amount) }
            }

            isUpdatingAmount = false
        }
    }

    private fun updateReceiveAmount(amount: Double) {
        if (isUpdatingAmount) return

        viewModelScope.launch {
            isUpdatingAmount = true

            _state.update { it.copy(receiveAmount = amount) }

            _state.value.exchangeRate?.let { rate ->
                if (rate.rate > 0) {
                    val sellAmount = round(amount / rate.rate)

                    if (abs(_state.value.sellAmount - sellAmount) > 0.001) {
                        _state.update { it.copy(sellAmount = sellAmount) }
                    }
                } else {
                    _state.update { it.copy(error = "Invalid exchange rate") }
                }
            } ?: run {
                _state.update { it.copy(sellAmount = amount) }
            }

            isUpdatingAmount = false
        }
    }

    private fun round(value: Double): Double = Math.round(value * 100) / 100.0

    private fun transfer(fromAccount: String, toAccount: String, amount: Double) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

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

            var success = false
            transferMoneyUseCase(fromAccount, toAccount, amount).collect { result ->
                when (result) {
                    is Resource.Success -> success = result.data.isSuccessful
                    is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.errorMessage) }
                    is Resource.Loading -> _state.update { it.copy(isLoading = result.isLoading) }
                }
            }

            if (success) {
                loadAccounts()
                resetInputValues()
                _state.update { it.copy(isLoading = false, transferSuccess = true, error = null) }
                _effect.send(TransferEffect.NavigateToSuccess("Transfer completed successfully"))
            } else {
                _state.update { it.copy(isLoading = false, error = "Failed to transfer money") }
            }
        }
    }

    private fun updateSelectedAccounts() {
        val accounts = _state.value.accounts
        val currentFromAccount = _state.value.fromAccount
        val currentToAccount = _state.value.toAccount

        if (accounts.isEmpty() || (currentFromAccount == null && currentToAccount == null)) {
            return
        }

        val updatedFromAccount = currentFromAccount?.let { selected ->
            accounts.find { it.accountNumber == selected.accountNumber }
        }

        val updatedToAccount = currentToAccount?.let { selected ->
            accounts.find { it.accountNumber == selected.accountNumber }
        }

        _state.update { state ->
            state.copy(
                fromAccount = updatedFromAccount ?: state.fromAccount,
                toAccount = updatedToAccount ?: state.toAccount
            )
        }
    }

    private fun resetInputValues() {
        _state.update {
            it.copy(
                sellAmount = 0.0,
                receiveAmount = 0.0,
                description = ""
            )
        }
    }

    fun showFromAccountBottomSheet() {
        viewModelScope.launch {
            _effect.send(TransferEffect.ShowFromAccountBottomSheet)
        }
    }
}