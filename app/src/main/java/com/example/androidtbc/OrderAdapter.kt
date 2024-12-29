package com.example.androidtbc

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.databinding.ItemOrderBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter(private val onDetailsClick: (Order) -> Unit) :
    ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    inner class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {
                tvOrderID.text = "Order #${order.orderId}"
                tvTrackingNumber.text = "Tracking number: ${order.trackingNumber}"
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(Date(order.dateInMillis))

                tvDate.text = formattedDate
                tvQuantity.text = "Quantity: ${order.quantity}"
                tvSubtotal.text = "Subtotal: $${order.subtotal}"
                tvStatusType.text = order.orderStatus.name

                tvStatusType.setTextColor(
                    when (order.orderStatus) {
                        StatusType.PENDING -> Color.parseColor("#CF6212")
                        StatusType.DELIVERED -> Color.parseColor("#009254")
                        StatusType.CANCELED -> Color.parseColor("#C50000")
                    }
                )

                btnDetails.setOnClickListener {
                    onDetailsClick(order)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem.orderId == newItem.orderId
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }
}
