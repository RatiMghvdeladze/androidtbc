package com.example.androidtbc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.databinding.ItemAddressBinding

class AddressDiffCallback : DiffUtil.ItemCallback<Address>() {
    override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
        return oldItem == newItem
    }
}
class AddressListAdapter : ListAdapter<Address, AddressListAdapter.AddressViewHolder>(AddressDiffCallback()) {

    private var onEditClickListener: ((Address) -> Unit)? = null
    private var onItemLongClickListener: ((Address) -> Unit)? = null

    fun setOnEditClickListener(listener: (Address) -> Unit) {
        onEditClickListener = listener
    }

    fun setOnItemLongClickListener(listener: (Address) -> Unit) {
        onItemLongClickListener = listener
    }

    inner class AddressViewHolder(
        private val binding: ItemAddressBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(address: Address) {
            binding.apply {
                iconAddress.setImageResource(
                    when (address.type) {
                        AddressType.Home -> R.drawable.icon_home
                        AddressType.Office -> R.drawable.icon_office
                    }
                )

                radioButton.isChecked = address.isSelected
                tvAddressType.text = address.type.name
                tvAddress.text = address.street
                btnEdit.isEnabled = address.isSelected

                btnEdit.setOnClickListener {
                    if (address.isSelected) {
                        onEditClickListener?.invoke(address)
                    }
                }

                radioButton.setOnClickListener {
                    updateSelection(adapterPosition)
                }

                root.setOnLongClickListener {
                    onItemLongClickListener?.invoke(address)
                    true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            ItemAddressBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun updateSelection(position: Int) {
        val currentList = currentList.toMutableList()
        currentList.forEachIndexed { index, item ->
            if (item.isSelected) {
                currentList[index] = item.copy(isSelected = false)
            }
        }
        currentList[position] = currentList[position].copy(isSelected = true)
        submitList(currentList)
    }

    fun addAddress(address: Address) {
        val currentList = currentList.toMutableList()
        currentList.add(0, address)
        submitList(currentList)
    }

    fun deleteAddress(address: Address) {
        val currentList = currentList.toMutableList()
        currentList.remove(address)
        submitList(currentList)
    }
}
