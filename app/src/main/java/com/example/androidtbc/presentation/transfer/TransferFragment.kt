package com.example.androidtbc.presentation.transfer

import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.androidtbc.R
import com.example.androidtbc.databinding.FragmentTransferBinding
import com.example.androidtbc.presentation.base.BaseFragment
import com.example.androidtbc.presentation.extension.launchLatest
import com.example.androidtbc.presentation.extension.showSnackbar
import com.example.androidtbc.presentation.utils.CardUtils
import com.example.androidtbc.presentation.utils.CurrencyUtils
import dagger.hilt.android.AndroidEntryPoint
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
            btnBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            cvFromAccount.setOnClickListener { viewModel.showFromAccountBottomSheet() }
            cvToAccount.setOnClickListener { showTransferTypeBottomSheet() }

            etSellAmount.doAfterTextChanged { text ->
                setAmountError(false)
                val amount = text?.toString()?.replace(",", "")?.toDoubleOrNull() ?: 0.0
                viewModel.onEvent(TransferEvent.UpdateSellAmount(amount))
            }

            etReceiveAmount.doAfterTextChanged { text ->
                val amount = text?.toString()?.replace(",", "")?.toDoubleOrNull() ?: 0.0
                viewModel.onEvent(TransferEvent.UpdateReceiveAmount(amount))
            }

            etDescription.doAfterTextChanged { text ->
                viewModel.onEvent(TransferEvent.UpdateDescription(text.toString()))
            }

            btnContinue.setOnClickListener {
                val state = viewModel.state.value

                if (state.sellAmount <= 0) {
                    setAmountError(true)
                    return@setOnClickListener
                }

                if (state.fromAccount != null && state.toAccount != null) {
                    viewModel.onEvent(
                        TransferEvent.Transfer(
                            fromAccount = state.fromAccount.accountNumber,
                            toAccount = state.toAccount.accountNumber,
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
        }
    }

    private fun setAmountError(isError: Boolean) {
        with(binding) {
            etSellAmount.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isError) R.color.error_red else R.color.white
                )
            )

            if (isError) {
                cvSellAmount.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.shake))
                tvAmountError.visibility = View.VISIBLE
            } else {
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
            progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            state.fromAccount?.let { account ->
                tvFromAccountName.text = "@${account.accountName}"
                tvFromAccountNumber.text = account.maskedNumber
                tvSellCurrency.text = CurrencyUtils.getCurrencySymbol(account.valuteType)
                tvFromAccountBalance.text = CurrencyUtils.formatAmountWithCurrency(account.balance, account.valuteType)
                ivFromCardLogo.setImageResource(CardUtils.getCardLogoResource(account.cardType))
            }

            state.toAccount?.let { account ->
                tvToAccountName.text = "@${account.accountName}"
                tvToAccountNumber.text = account.maskedNumber
                tvReceiveCurrency.text = CurrencyUtils.getCurrencySymbol(account.valuteType)
                tvToAccountBalance.text = CurrencyUtils.formatAmountWithCurrency(account.balance, account.valuteType)
                ivToCardLogo.setImageResource(CardUtils.getCardLogoResource(account.cardType))
            }

            cvReceiveAmount.visibility = if (state.showDifferentCurrencyInputs) View.VISIBLE else View.GONE

            if (!etSellAmount.hasFocus() && etSellAmount.text.toString().toDoubleOrNull() != state.sellAmount) {
                etSellAmount.setText(formatAmount(state.sellAmount))
            }

            if (!etReceiveAmount.hasFocus() && etReceiveAmount.text.toString().toDoubleOrNull() != state.receiveAmount) {
                etReceiveAmount.setText(formatAmount(state.receiveAmount))
            }

            tvExchangeRate.apply {
                visibility = if (state.exchangeRate != null) View.VISIBLE else View.GONE
                text = state.exchangeRate?.displayText
            }

            if (state.description.isNotEmpty() && etDescription.text.toString() != state.description) {
                etDescription.setText(state.description)
            }

            state.error?.let {
                root.showSnackbar(it, R.color.card_background, R.color.white)
                viewModel.onEvent(TransferEvent.ClearError)
            }
        }
    }

    private fun handleEffect(effect: TransferEffect) {
        when (effect) {
            is TransferEffect.ShowSnackbar -> {
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

                resetInputFields()
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

    private fun resetInputFields() {
        binding.etSellAmount.text = null
        binding.etReceiveAmount.text = null
        binding.etDescription.text = null
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
        val rootView = activity?.window?.decorView?.findViewById<ViewGroup>(android.R.id.content) ?: return
        val successIndicatorLayout = layoutInflater.inflate(R.layout.success_indicator, rootView, false)

        rootView.addView(successIndicatorLayout)

        successIndicatorLayout.alpha = 0f
        successIndicatorLayout.animate()
            .alpha(1f)
            .setDuration(300)
            .start()

        delayedAction(2000) {
            successIndicatorLayout.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    rootView.removeView(successIndicatorLayout)
                    viewModel.onEvent(TransferEvent.LoadAccounts)

                    fromAccountBottomSheet?.takeIf { it.isAdded }?.let {
                        delayedAction(200) {
                            it.refreshAccounts(viewModel.state.value.accounts)
                        }
                    }
                }
                .start()
        }
    }

    private fun delayedAction(delayMillis: Long, action: () -> Unit) {
        lifecycleScope.launch {
            delay(delayMillis)
            action()
        }
    }

    private fun formatAmount(amount: Double): String =
        if (amount > 0) numberFormatter.format(amount) else ""
}