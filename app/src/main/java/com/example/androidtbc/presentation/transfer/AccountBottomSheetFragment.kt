package com.example.androidtbc.presentation.transfer

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtbc.databinding.BottomSheetAccountsBinding
import com.example.androidtbc.presentation.model.AccountUI
import com.example.androidtbc.presentation.transfer.adapter.AccountAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AccountBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAccountsBinding? = null
    private val binding get() = _binding!!

    private var accounts: List<AccountUI> = emptyList()
    private var onAccountSelected: ((AccountUI) -> Unit)? = null

    private val accountAdapter by lazy {
        AccountAdapter { account -> onAccountSelected?.invoke(account) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accounts = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList(ARG_ACCOUNTS, AccountUI::class.java) ?: emptyList()
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelableArrayList(ARG_ACCOUNTS) ?: emptyList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvAccounts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = accountAdapter
        }
        accountAdapter.submitList(accounts)
    }

    fun refreshAccounts(updatedAccounts: List<AccountUI>) {
        if (updatedAccounts.isNotEmpty()) {
            accounts = updatedAccounts
            accountAdapter.submitList(updatedAccounts)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ACCOUNTS = "accounts"

        fun newInstance(accounts: List<AccountUI>, onAccountSelected: (AccountUI) -> Unit) =
            AccountBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_ACCOUNTS, ArrayList(accounts))
                }
                this.onAccountSelected = onAccountSelected
            }
    }
}