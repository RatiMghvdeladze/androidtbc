package com.example.androidtbc.presentation.transfer

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentTransferBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
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

    override fun start() {
        setupListeners()
        setupObservers()
        viewModel.onEvent(TransferEvent.LoadAccounts)
    }

    private fun setupListeners() {
        with(binding) {
            btnBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            cvFromAccount.setOnClickListener {
                viewModel.showFromAccountBottomSheet()
            }

            cvToAccount.setOnClickListener {
                showTransferTypeBottomSheet()
            }

            // Use afterTextChanged to avoid triggering during programmatic changes
            var ignoreTextChange = false

            etSellAmount.doAfterTextChanged { text ->
                if (!ignoreTextChange && text != null) {
                    try {
                        val amount = text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
                        viewModel.onEvent(TransferEvent.UpdateSellAmount(amount))
                    } catch (e: Exception) {
                        Log.e("TransferFragment", "Error parsing sell amount: ${e.message}")
                    }
                }
            }

            etReceiveAmount.doAfterTextChanged { text ->
                if (!ignoreTextChange && text != null) {
                    try {
                        val amount = text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
                        viewModel.onEvent(TransferEvent.UpdateReceiveAmount(amount))
                    } catch (e: Exception) {
                        Log.e("TransferFragment", "Error parsing receive amount: ${e.message}")
                    }
                }
            }

            etDescription.doAfterTextChanged { text ->
                viewModel.onEvent(TransferEvent.UpdateDescription(text.toString()))
            }

            btnContinue.setOnClickListener {
                val state = viewModel.state.value
                val fromAccount = state.fromAccount
                val toAccount = state.toAccount

                if (fromAccount != null && toAccount != null) {
                    viewModel.onEvent(
                        TransferEvent.Transfer(
                            fromAccount = fromAccount.accountNumber,
                            toAccount = toAccount.accountNumber,
                            amount = state.sellAmount
                        )
                    )
                } else {
                    showSnackbar("Please select accounts")
                }
            }
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    updateUI(state)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collectLatest { effect ->
                    handleEffect(effect)
                }
            }
        }
    }

    // You'll need to update your TransferViewModel to handle the display of account balances
// Here's a code snippet to update in your updateUI method in TransferFragment.kt:

    private fun updateUI(state: TransferState) {
        with(binding) {
            // Update loading state
            progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            // Update from account
            state.fromAccount?.let { account ->
                tvFromAccountName.text = "@${account.accountName}"
                tvFromAccountNumber.text = account.maskedNumber
                tvSellCurrency.text = getCurrencySymbol(account.valuteType)
                tvFromAccountBalance.text = "${account.balance} ${getCurrencySymbol(account.valuteType)}"

                // Set card logo based on card type
                when (account.cardType) {
                    "VISA" -> ivFromCardLogo.setImageResource(R.drawable.ic_visa)
                    "MASTER_CARD" -> ivFromCardLogo.setImageResource(R.drawable.ic_mastercard)
                    else -> ivFromCardLogo.setImageResource(R.drawable.ic_visa) // Default
                }
            }

            // Update to account
            state.toAccount?.let { account ->
                tvToAccountName.text = "@${account.accountName}"
                tvToAccountNumber.text = account.maskedNumber
                tvReceiveCurrency.text = getCurrencySymbol(account.valuteType)
                tvToAccountBalance.text = "${account.balance} ${getCurrencySymbol(account.valuteType)}"

                // Set card logo based on card type
                when (account.cardType) {
                    "VISA" -> ivToCardLogo.setImageResource(R.drawable.ic_visa)
                    "MASTER_CARD" -> ivToCardLogo.setImageResource(R.drawable.ic_mastercard)
                    else -> ivToCardLogo.setImageResource(R.drawable.ic_visa) // Default
                }
            }

            // Update currency inputs visibility
            if (state.showSameCurrencyInput) {
                cvSellAmount.visibility = View.VISIBLE
                cvReceiveAmount.visibility = View.GONE
            } else if (state.showDifferentCurrencyInputs) {
                cvSellAmount.visibility = View.VISIBLE
                cvReceiveAmount.visibility = View.VISIBLE
            }

            // Update exchange rate info
            state.exchangeRate?.let { exchangeRate ->
                tvExchangeRate.text = exchangeRate.displayText
                tvExchangeRate.visibility = View.VISIBLE
            } ?: run {
                tvExchangeRate.visibility = View.GONE
            }

            // Update amounts if they have changed programmatically
            val formatter = NumberFormat.getNumberInstance(Locale.US)
            formatter.maximumFractionDigits = 2

            if (etSellAmount.text.toString().toDoubleOrNull() != state.sellAmount) {
                etSellAmount.setText(if (state.sellAmount > 0) formatter.format(state.sellAmount) else "")
            }

            if (etReceiveAmount.text.toString().toDoubleOrNull() != state.receiveAmount) {
                etReceiveAmount.setText(if (state.receiveAmount > 0) formatter.format(state.receiveAmount) else "")
            }

            // Show error if any
            state.error?.let { error ->
                showSnackbar(error)
                viewModel.onEvent(TransferEvent.ClearError)
            }
        }
    }

    private fun handleEffect(effect: TransferEffect) {
        when (effect) {
            is TransferEffect.ShowToast -> {
                showSnackbar(effect.message)
            }
            is TransferEffect.NavigateToSuccess -> {
                // Show success message
                showSnackbar("Transfer completed successfully")

                // First, reload accounts to ensure we have the most current data
                viewModel.onEvent(TransferEvent.LoadAccounts)

                // Clear input fields
                binding.etSellAmount.setText("")
                binding.etReceiveAmount.setText("")
                binding.etDescription.setText("")

                // Force an immediate UI update to show the new balances
                viewModel.state.value.let { updateUI(it) }

                // Optional: Show a more prominent success indicator
                showSuccessIndicator()
            }
            is TransferEffect.ShowInsufficientFundsError -> {
                showSnackbar("Insufficient funds in your account")
            }
            is TransferEffect.ShowFromAccountBottomSheet -> {
                // Force reload accounts before showing the sheet to ensure we have latest data
                viewModel.onEvent(TransferEvent.LoadAccounts)

                // Use a small delay to ensure the accounts are loaded before showing the sheet
                Handler(Looper.getMainLooper()).postDelayed({
                    showFromAccountBottomSheet()
                }, 100)
            }
        }
    }

    private fun getCurrencySymbol(currencyCode: String): String {
        return when (currencyCode) {
            "GEL" -> "₾"
            "EUR" -> "€"
            "USD" -> "$"
            else -> currencyCode
        }
    }


    private fun showSuccessIndicator() {
        // Create and show a temporary success indicator
        val successIndicator = View.inflate(requireContext(), R.layout.success_indicator, null)

        // Add layout params to ensure it fills the container
        val layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Get the root container (should be a ViewGroup)
        val container = binding.root as ViewGroup

        // Add the view with proper layout params
        container.addView(successIndicator, layoutParams)

        // Animate and remove after a delay
        successIndicator.alpha = 0f
        successIndicator.animate()
            .alpha(1f)
            .setDuration(300)
            .start()

        // Remove after 2 seconds and force refresh the UI
        successIndicator.postDelayed({
            successIndicator.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    container.removeView(successIndicator)

                    // Force a complete refresh by reloading accounts
                    Log.d("TransferFragment", "Success indicator dismissed - Forcing account reload")
                    viewModel.onEvent(TransferEvent.LoadAccounts)

                    // If we have any open bottom sheets, update them as well
                    if (fromAccountBottomSheet != null && fromAccountBottomSheet?.isAdded == true) {
                        // Give time for accounts to reload
                        Handler(Looper.getMainLooper()).postDelayed({
                            Log.d("TransferFragment", "Updating bottom sheet accounts after success")
                            fromAccountBottomSheet?.refreshAccounts(viewModel.state.value.accounts)
                        }, 200)
                    }
                }
                .start()
        }, 2000)
    }
    // Use Snackbar instead of Toast to avoid the SystemUI error
    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.card_background))
        snackbar.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        snackbar.show()
    }

    // In your TransferFragment, update the showFromAccountBottomSheet method:
    // In your TransferFragment class, modify the showFromAccountBottomSheet method:

    /**
     * Shows the account selection bottom sheet with the most up-to-date account data
     * Forces a reload of accounts to ensure balance changes are displayed
     */
    private fun showFromAccountBottomSheet() {
        // Force a reload to get updated account balances
        viewModel.onEvent(TransferEvent.LoadAccounts)

        // Use a short delay to allow accounts to load
        Handler(Looper.getMainLooper()).postDelayed({
            val accounts = viewModel.state.value.accounts

            if (accounts.isNotEmpty()) {
                // Log account details for debugging
                for (account in accounts) {
                    Log.d("TransferFragment", "Account: ${account.accountName}, Balance: ${account.balance}")
                }

                // Create a new bottom sheet instance with the latest account data
                fromAccountBottomSheet = AccountBottomSheetFragment.newInstance(accounts) { account ->
                    viewModel.onEvent(TransferEvent.SelectFromAccount(account))
                    fromAccountBottomSheet?.dismiss()
                }

                fromAccountBottomSheet?.show(parentFragmentManager, "FromAccountBottomSheet")
            } else {
                showSnackbar("No accounts available")
            }
        }, 200) // Small delay to ensure accounts are loaded
    }
    private fun showTransferTypeBottomSheet() {
        transferTypeBottomSheet = TransferTypeBottomSheetFragment.newInstance { type, input ->
            when (type) {
                "ACCOUNT_NUMBER" -> {
                    viewModel.onEvent(TransferEvent.ValidateAccount(input, "ACCOUNT_NUMBER"))
                }
                "PERSONAL_ID" -> {
                    viewModel.onEvent(TransferEvent.ValidateAccount(input, "PERSONAL_ID"))
                }
                "PHONE_NUMBER" -> {
                    viewModel.onEvent(TransferEvent.ValidateAccount(input, "PHONE_NUMBER"))
                }
            }
            transferTypeBottomSheet?.dismiss()
        }
        transferTypeBottomSheet?.show(parentFragmentManager, "TransferTypeBottomSheet")
    }

    companion object {
        fun newInstance() = TransferFragment()
    }
}