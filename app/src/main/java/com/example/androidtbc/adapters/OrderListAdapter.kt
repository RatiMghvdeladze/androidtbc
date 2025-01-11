package com.example.androidtbc.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.Order
import com.example.androidtbc.databinding.DialogLeaveReviewBinding
import com.example.androidtbc.databinding.ItemOrderBinding
import com.example.androidtbc.updateColorCircle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar


class OrderDiffCallBack : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }

}

class OrderListAdapter : ListAdapter<Order, OrderListAdapter.OrderViewHolder>(OrderDiffCallBack()){

    inner class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order){
            binding.apply {
                tvPrice.text = "$${order.price}0"
                tvTitle.text = order.title
                ivPhoto.setImageResource(order.image)
                tvCompletedOrActive.text = order.status.name
                tvColorName.text = order.color
                tvQty.text = "Qty = ${order.quantity}"

                val colorCircle = updateColorCircle(itemView.context, order.color)
                ivCircleColor.background = colorCircle
                btnLeaveReview.setOnClickListener {
                    showReviewDialog(itemView.context, order)
                }
            }



        }

    }
    private fun showReviewDialog(context: Context, order: Order) {
        val bottomSheetDialog =
            BottomSheetDialog(context, com.example.androidtbc.R.style.BottomSheetDialogTheme)
        val dialogBinding = DialogLeaveReviewBinding.inflate(LayoutInflater.from(context))

        bottomSheetDialog.setContentView(dialogBinding.root)

        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
            }
        }

        val colorCircle = updateColorCircle(context, order.color)
        dialogBinding.ivCircleColor.background = colorCircle

        dialogBinding.apply {
            tvPrice.text = "$${order.price}0"
            tvTitle.text = order.title
            ivPhoto.setImageResource(order.image)
            tvCompletedOrActive.text = order.status.name
            tvColorName.text = order.color
            tvQty.text = "Qty = ${order.quantity}"


            btnCancel.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            btnSubmit.setOnClickListener {
                bottomSheetDialog.dismiss()
                val rootView = (context as AppCompatActivity).window.decorView.findViewById<View>(android.R.id.content)
                if (dialogBinding.etReview.text.toString().isNotEmpty()) {
                    Snackbar.make(rootView, "Feedback sent successfully", Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(rootView, "Feedback was empty didn't send", Snackbar.LENGTH_SHORT).show()
                }
            }




            bottomSheetDialog.show()
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