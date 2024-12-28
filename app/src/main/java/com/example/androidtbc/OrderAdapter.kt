package com.example.androidtbc

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.databinding.ItemOrderBinding

class OrderAdapter : ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallBack()){
    inner class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order){
            binding.apply{
                tvOrderID.text = "Order #${order.orderId}"
                tvTrackingNumber.text = "Tracking number: ${order.trackingNumber}"
                tvDate.text = order.date
                tvQuantity.text = "Quantity: ${order.quantity}"
                tvSubtotal.text = "Subtotal: $${order.subtotal}"
                tvStatusType.text = order.orderStatus.name

                tvStatusType.setTextColor(
                    when(order.orderStatus){
                        StatusType.PENDING -> Color.parseColor("#CF6212")
                        StatusType.DELIVERED -> Color.parseColor("#009254")
                        StatusType.CANCELED -> Color.parseColor("#C50000")
                    }
                )

                btnDetails.setOnClickListener{

                }

            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}

class OrderDiffCallBack: DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem.orderId == newItem.orderId
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }


}
