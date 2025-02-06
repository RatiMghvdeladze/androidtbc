package com.example.androidtbc.ui.passcode.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.databinding.ItemPasscodeCircleBinding

class PasscodeCircleAdapter : ListAdapter<Boolean, PasscodeCircleAdapter.CircleViewHolder>(
    CircleDiffCallback()
){

    inner class CircleViewHolder(private val binding: ItemPasscodeCircleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val isFilled = getItem(position)
            binding.ivCircle.isSelected = isFilled
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircleViewHolder {
        return CircleViewHolder(
            ItemPasscodeCircleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CircleViewHolder, position: Int) {
        holder.bind(position)
    }

}

class CircleDiffCallback : DiffUtil.ItemCallback<Boolean>() {
    override fun areItemsTheSame(oldItem: Boolean, newItem: Boolean): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Boolean, newItem: Boolean): Boolean {
        return oldItem == newItem
    }

}
