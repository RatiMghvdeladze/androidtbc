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
import com.example.androidtbc.presentation.model.CardTypeUI
import com.example.androidtbc.presentation.model.CurrencyTypeUI
import com.example.androidtbc.presentation.model.ExchangeRateUI
import com.example.androidtbc.presentation.model.ValidationTypeUI
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
            is TransferEvent.ClearInputs -> _state.update { it.copy(sellAmount = 0.0, receiveAmount = 0.0, description = "") }
        }
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getAccountsUseCase()
                .catch { exception ->
                    _state.update { it.copy(isLoading = false, error = exception.message ?: "Unknown error occurred") }
                }
                .collectLatest { result ->
                    when (result) {
                        is Resource.Success -> {
                            val accountsUI = result.data.map { AccountUI.fromDomain(it) }
                            _state.update { it.copy(isLoading = false, accounts = accountsUI, error = null) }
                            updateSelectedAccounts()
                        }
                        is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.errorMessage) }
                        is Resource.Loading -> _state.update { it.copy(isLoading = result.isLoading) }
                    }
                }
        }
    }

    private fun selectFromAccount(account: AccountUI) {
        viewModelScope.launch {
            if (_state.value.toAccount?.accountNumber == account.accountNumber) {
                _state.update { it.copy(error = "Cannot transfer from the same account") }
                _effect.send(TransferEffect.ShowSnackbar("Please select a different account"))
                return@launch
            }

            _state.update { it.copy(fromAccount = account, error = null) }
            _state.value.toAccount?.let { toAccount -> checkCurrencies(account, toAccount) }
        }
    }

    private fun selectToAccount(account: AccountUI) {
        viewModelScope.launch {
            if (_state.value.fromAccount?.accountNumber == account.accountNumber) {
                _state.update { it.copy(error = "Cannot transfer to the same account") }
                _effect.send(TransferEffect.ShowSnackbar("Please select a different account"))
                return@launch
            }

            _state.update { it.copy(toAccount = account, error = null) }
            _state.value.fromAccount?.let { fromAccount -> checkCurrencies(fromAccount, account) }
        }
    }

    private fun checkCurrencies(fromAccount: AccountUI, toAccount: AccountUI) {
        viewModelScope.launch {
            val sameCurrency = fromAccount.valuteType == toAccount.valuteType
            _state.update {
                it.copy(
                    showSameCurrencyInput = sameCurrency,
                    showDifferentCurrencyInputs = !sameCurrency,
                    exchangeRate = if (sameCurrency) null else it.exchangeRate
                )
            }

            if (sameCurrency) {
                updateSellAmount(_state.value.sellAmount)
            } else {
                fetchExchangeRate(fromAccount.valuteType, toAccount.valuteType)
            }
        }
    }

    private fun validateAccount(accountNumber: String, validationTypeUI: ValidationTypeUI) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            validateAccountUseCase(accountNumber, validationTypeUI.toDomain())
                .catch { exception ->
                    _state.update { it.copy(isLoading = false, error = exception.message ?: "Validation failed") }
                }
                .collectLatest { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.update { it.copy(isLoading = false, accountValidation = result.data, error = null) }

                            if (result.data.isValid) {
                                val matchingAccount = if (validationTypeUI == ValidationTypeUI.ACCOUNT_NUMBER) {
                                    _state.value.accounts.find {
                                        it.accountNumber.replace(" ", "") == accountNumber.replace(" ", "")
                                    }
                                } else null

                                matchingAccount?.let {
                                    selectToAccount(it)
                                } ?: createTemporaryToAccount(accountNumber, validationTypeUI)
                            } else {
                                _state.update { it.copy(error = "Invalid account") }
                            }
                        }
                        is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.errorMessage) }
                        is Resource.Loading -> _state.update { it.copy(isLoading = result.isLoading) }
                    }
                }
        }
    }

    private fun createTemporaryToAccount(identifier: String, typeUI: ValidationTypeUI) {
        val name = when (typeUI) {
            ValidationTypeUI.ACCOUNT_NUMBER -> "External Account"
            ValidationTypeUI.PERSONAL_ID -> "ID: ${identifier.take(4)}****${identifier.takeLast(3)}"
            ValidationTypeUI.PHONE_NUMBER -> "Phone: ${identifier.take(3)}****${identifier.takeLast(2)}"
        }

        val maskedNumber = if (identifier.length > 4) "**** ${identifier.takeLast(4)}" else identifier
        val defaultCurrencyType = _state.value.fromAccount?.valuteType ?: CurrencyTypeUI.GEL

        val tempAccountUI = AccountUI(
            id = -1,
            accountName = name,
            accountNumber = identifier,
            valuteType = defaultCurrencyType,
            cardType = CardTypeUI.VISA,
            balance = 0.0,
            cardLogo = null,
            maskedNumber = maskedNumber
        )

        selectToAccount(tempAccountUI)
    }

    private fun fetchExchangeRate(fromCurrencyUI: CurrencyTypeUI, toCurrencyUI: CurrencyTypeUI) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getExchangeRateUseCase(fromCurrencyUI.toDomain(), toCurrencyUI.toDomain())
                .catch { _state.update { it.copy(isLoading = false) } }
                .collectLatest { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.update { it.copy(
                                isLoading = false,
                                exchangeRate = ExchangeRateUI.fromDomain(result.data),
                                error = null
                            )}
                            updateSellAmount(_state.value.sellAmount)
                        }
                        is Resource.Error -> _state.update { it.copy(isLoading = false) }
                        is Resource.Loading -> _state.update { it.copy(isLoading = result.isLoading) }
                    }
                }
        }
    }

    private fun updateSellAmount(amount: Double) {
        if (isUpdatingAmount) return

        viewModelScope.launch {
            isUpdatingAmount = true

            _state.update { it.copy(sellAmount = amount) }

            val receiveAmount = _state.value.exchangeRate?.let {
                round(amount * it.rate)
            } ?: amount

            if (abs(_state.value.receiveAmount - receiveAmount) > 0.001) {
                _state.update { it.copy(receiveAmount = receiveAmount) }
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
            if (fromAccount == toAccount) {
                _state.update { it.copy(error = "Cannot transfer to the same account") }
                _effect.send(TransferEffect.ShowSnackbar("Source and destination accounts cannot be the same"))
                return@launch
            }

            _state.update { it.copy(isLoading = true) }

            val sourceAccount = accountManager.getAccount(fromAccount) ?: run {
                _state.update { it.copy(isLoading = false, error = "Source account not found") }
                return@launch
            }

            if (sourceAccount.balance < amount) {
                _state.update { it.copy(isLoading = false) }
                _effect.send(TransferEffect.ShowInsufficientFundsError)
                return@launch
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
                onEvent(TransferEvent.ClearInputs)
                _state.update { it.copy(isLoading = false, transferSuccess = true, error = null) }
                _effect.send(TransferEffect.NavigateToSuccess("Transfer completed successfully"))
                loadAccounts()
            } else {
                _state.update { it.copy(isLoading = false, error = "Failed to transfer money") }
            }
        }
    }

    private fun updateSelectedAccounts() {
        if (_state.value.accounts.isEmpty() || (_state.value.fromAccount == null && _state.value.toAccount == null)) return

        val accounts = _state.value.accounts
        val fromAccount = _state.value.fromAccount
        val toAccount = _state.value.toAccount

        val updatedFromAccount = fromAccount?.let { from -> accounts.find { it.accountNumber == from.accountNumber } }
        val updatedToAccount = toAccount?.let { to -> accounts.find { it.accountNumber == to.accountNumber } }

        _state.update { state ->
            state.copy(
                fromAccount = updatedFromAccount ?: state.fromAccount,
                toAccount = updatedToAccount ?: state.toAccount
            )
        }
    }

    fun showFromAccountBottomSheet() {
        viewModelScope.launch {
            _effect.send(TransferEffect.ShowFromAccountBottomSheet)
        }
    }
}