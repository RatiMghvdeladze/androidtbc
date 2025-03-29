package com.example.androidtbc.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.R
import com.example.androidtbc.databinding.ItemAccountBinding
import com.example.androidtbc.presentation.model.AccountUI

/**
 * Adapter for displaying account items in RecyclerView
 * Includes special handling for account balance updates
 */
class AccountAdapter(
    private val onAccountSelected: (AccountUI) -> Unit
) : ListAdapter<AccountUI, AccountAdapter.AccountViewHolder>(AccountDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = ItemAccountBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AccountViewHolder(
        private val binding: ItemAccountBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAccountSelected(getItem(position))
                }
            }
        }

        fun bind(account: AccountUI) {
            binding.apply {
                tvAccountName.text = account.accountName
                tvAccountNumber.text = account.maskedNumber
                tvBalance.text = "${account.balance} ${account.valuteType}"

                // Set card logo based on card type
                ivCardLogo.setImageResource(when (account.cardType) {
                    "MASTER_CARD" -> R.drawable.ic_mastercard
                    else -> R.drawable.ic_visa // Default and VISA case
                })
            }
        }
    }

    private class AccountDiffCallback : DiffUtil.ItemCallback<AccountUI>() {
        override fun areItemsTheSame(oldItem: AccountUI, newItem: AccountUI): Boolean {
            return oldItem.id == newItem.id && oldItem.accountNumber == newItem.accountNumber
        }

        override fun areContentsTheSame(oldItem: AccountUI, newItem: AccountUI): Boolean {
            // Include balance in comparison to detect balance changes
            return oldItem.balance == newItem.balance &&
                    oldItem.accountName == newItem.accountName &&
                    oldItem.valuteType == newItem.valuteType &&
                    oldItem.cardType == newItem.cardType
        }
    }

    // Override to ensure DiffUtil runs properly with balance changes
    override fun submitList(list: List<AccountUI>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}