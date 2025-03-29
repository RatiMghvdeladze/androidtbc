package com.example.androidtbc.presentation.transfer

import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentTransferBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.extension.launchLatest
import com.example.androidtbc.presentation.extension.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class TransferFragment : BaseFragment<FragmentTransferBinding>(
    FragmentTransferBinding::inflate
) {
    private val viewModel: TransferViewModel by viewModels()
    private var fromAccountBottomSheet: AccountBottomSheetFragment? = null
    private var transferTypeBottomSheet: TransferTypeBottomSheetFragment? = null

    // Number formatter for currency display
    private val numberFormatter = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 2
    }

    override fun start() {
        setupListeners()
        setupObservers()
        viewModel.onEvent(TransferEvent.LoadAccounts)
    }

    private fun setupListeners() {
        with(binding) {
            // Navigation - using modern approach instead of deprecated onBackPressed()
            btnBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            // Account selection
            cvFromAccount.setOnClickListener { viewModel.showFromAccountBottomSheet() }
            cvToAccount.setOnClickListener { showTransferTypeBottomSheet() }

// Updated etSellAmount text change listener
            etSellAmount.doAfterTextChanged { text ->
                setAmountError(false)
                val amount = text?.toString()?.replace(",", "")?.toDoubleOrNull() ?: 0.0
                viewModel.onEvent(TransferEvent.UpdateSellAmount(amount))
            }

// Updated etReceiveAmount text change listener
            etReceiveAmount.doAfterTextChanged { text ->
                val amount = text?.toString()?.replace(",", "")?.toDoubleOrNull() ?: 0.0
                viewModel.onEvent(TransferEvent.UpdateReceiveAmount(amount))
            }

            // Description and transfer action
            etDescription.doAfterTextChanged { text ->
                viewModel.onEvent(TransferEvent.UpdateDescription(text.toString()))
            }
            btnContinue.setOnClickListener {
                val state = viewModel.state.value
                val fromAccount = state.fromAccount
                val toAccount = state.toAccount

                // Validate that an amount is entered
                if (state.sellAmount <= 0) {
                    setAmountError(true)
                    return@setOnClickListener
                } else {
                    setAmountError(false)
                }

                if (fromAccount != null && toAccount != null) {
                    viewModel.onEvent(
                        TransferEvent.Transfer(
                            fromAccount = fromAccount.accountNumber,
                            toAccount = toAccount.accountNumber,
                            amount = state.sellAmount
                        )
                    )
                } else {
                    root.showSnackbar(
                        "Please select accounts",
                        backgroundColorResId = R.color.card_background,
                        textColorResId = R.color.white
                    )
                }
            }

// Also add this to etSellAmount.doAfterTextChanged to clear error when user starts typing
            etSellAmount.doAfterTextChanged { text ->
                setAmountError(false)
                text?.toString()?.replace(",", "")?.toDoubleOrNull()?.let { amount ->
                    viewModel.onEvent(TransferEvent.UpdateSellAmount(amount))
                }
            }
        }
    }

    private fun setAmountError(isError: Boolean) {
        with(binding) {
            if (isError) {
                // Apply error state
                etSellAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.error_red))

                // Create a shake animation for better feedback
                val shakeAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
                cvSellAmount.startAnimation(shakeAnimation)

                // Show error message below the field
                tvAmountError.visibility = View.VISIBLE
                tvAmountError.text = "Please enter an amount"
            } else {
                // Clear error state
                etSellAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                tvAmountError.visibility = View.GONE
            }
        }
    }

    private fun setupObservers() {
        launchLatest(viewModel.state) { updateUI(it) }
        launchLatest(viewModel.effect) { handleEffect(it) }
    }

    private fun updateUI(state: TransferState) {
        with(binding) {
            // Loading indicator
            progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            // From account display
            state.fromAccount?.let { account ->
                tvFromAccountName.text = "@${account.accountName}"
                tvFromAccountNumber.text = account.maskedNumber
                tvSellCurrency.text = getCurrencySymbol(account.valuteType)
                tvFromAccountBalance.text = "${account.balance} ${getCurrencySymbol(account.valuteType)}"

                // Card logo
                ivFromCardLogo.setImageResource(getCardLogo(account.cardType))
            }

            // To account display
            state.toAccount?.let { account ->
                tvToAccountName.text = "@${account.accountName}"
                tvToAccountNumber.text = account.maskedNumber
                tvReceiveCurrency.text = getCurrencySymbol(account.valuteType)
                tvToAccountBalance.text = "${account.balance} ${getCurrencySymbol(account.valuteType)}"

                // Card logo
                ivToCardLogo.setImageResource(getCardLogo(account.cardType))
            }

            // Currency inputs visibility
            cvReceiveAmount.visibility = if (state.showDifferentCurrencyInputs) View.VISIBLE else View.GONE

            // Update input fields if needed - only if user is not currently editing
            if (!etSellAmount.hasFocus() && etSellAmount.text.toString().toDoubleOrNull() != state.sellAmount) {
                etSellAmount.setText(formatAmount(state.sellAmount))
            }

            if (!etReceiveAmount.hasFocus() && etReceiveAmount.text.toString().toDoubleOrNull() != state.receiveAmount) {
                etReceiveAmount.setText(formatAmount(state.receiveAmount))
            }

            // Exchange rate
            tvExchangeRate.apply {
                visibility = if (state.exchangeRate != null) View.VISIBLE else View.GONE
                text = state.exchangeRate?.displayText
            }

            // Description
            if (state.description.isNotEmpty() && etDescription.text.toString() != state.description) {
                etDescription.setText(state.description)
            }

            // Handle errors
            state.error?.let {
                root.showSnackbar(
                    it,
                    backgroundColorResId = R.color.card_background,
                    textColorResId = R.color.white
                )
                viewModel.onEvent(TransferEvent.ClearError)
            }
        }
    }

    private fun handleEffect(effect: TransferEffect) {
        when (effect) {
            is TransferEffect.ShowToast -> {
                binding.root.showSnackbar(
                    effect.message,
                    backgroundColorResId = R.color.card_background,
                    textColorResId = R.color.white
                )
            }

            is TransferEffect.NavigateToSuccess -> {
                binding.root.showSnackbar(
                    "Transfer completed successfully",
                    backgroundColorResId = R.color.card_background,
                    textColorResId = R.color.white
                )

                // Reset input fields directly in UI
                binding.etSellAmount.text = null
                binding.etReceiveAmount.text = null
                binding.etDescription.text = null

                // Reload accounts and show success indicator
                viewModel.onEvent(TransferEvent.LoadAccounts)
                showSuccessIndicator()
            }

            is TransferEffect.ShowInsufficientFundsError -> {
                binding.root.showSnackbar(
                    "Insufficient funds in your account",
                    backgroundColorResId = R.color.card_background,
                    textColorResId = R.color.white
                )
            }

            is TransferEffect.ShowFromAccountBottomSheet -> {
                viewModel.onEvent(TransferEvent.LoadAccounts)
                delayedAction(100) { showFromAccountBottomSheet() }
            }
        }
    }

    private fun showFromAccountBottomSheet() {
        val accounts = viewModel.state.value.accounts
        if (accounts.isEmpty()) {
            binding.root.showSnackbar(
                "No accounts available",
                backgroundColorResId = R.color.card_background,
                textColorResId = R.color.white
            )
            return
        }

        fromAccountBottomSheet = AccountBottomSheetFragment.newInstance(accounts) { account ->
            viewModel.onEvent(TransferEvent.SelectFromAccount(account))
            fromAccountBottomSheet?.dismiss()
        }

        fromAccountBottomSheet?.show(parentFragmentManager, "FromAccountBottomSheet")
    }

    private fun showTransferTypeBottomSheet() {
        transferTypeBottomSheet = TransferTypeBottomSheetFragment.newInstance { type, input ->
            viewModel.onEvent(TransferEvent.ValidateAccount(input, type))
            transferTypeBottomSheet?.dismiss()
        }
        transferTypeBottomSheet?.show(parentFragmentManager, "TransferTypeBottomSheet")
    }

    private fun showSuccessIndicator() {
        // Get the root view of the activity
        val rootView = activity?.window?.decorView?.findViewById<ViewGroup>(android.R.id.content)
            ?: return

        // Inflate the success indicator layout directly
        val successIndicatorLayout = layoutInflater.inflate(R.layout.success_indicator, rootView, false)

        // The layout already has clickable=true and focusable=true in the XML

        // Add the success indicator to the root view
        rootView.addView(successIndicatorLayout)

        // Handle animation - fade in
        successIndicatorLayout.alpha = 0f
        successIndicatorLayout.animate()
            .alpha(1f)
            .setDuration(300)
            .start()

        // Remove after delay with fade out
        delayedAction(2000) {
            successIndicatorLayout.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    // Remove the success indicator
                    rootView.removeView(successIndicatorLayout)

                    // Reload accounts
                    viewModel.onEvent(TransferEvent.LoadAccounts)

                    // Update bottom sheet if open
                    if (fromAccountBottomSheet?.isAdded == true) {
                        delayedAction(200) {
                            fromAccountBottomSheet?.refreshAccounts(viewModel.state.value.accounts)
                        }
                    }
                }
                .start()
        }
    }

    // Helper methods
    private fun delayedAction(delayMillis: Long, action: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(delayMillis)
            action()
        }
    }

    private fun formatAmount(amount: Double): String {
        return if (amount > 0) numberFormatter.format(amount) else ""
    }

    private fun getCurrencySymbol(currencyCode: String): String = when (currencyCode) {
        "GEL" -> "₾"
        "EUR" -> "€"
        "USD" -> "$"
        else -> currencyCode
    }

    private fun getCardLogo(cardType: String): Int = when (cardType) {
        "MASTER_CARD" -> R.drawable.ic_mastercard
        else -> R.drawable.ic_visa // Default and "VISA" case
    }
}