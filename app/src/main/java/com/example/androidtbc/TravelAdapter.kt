package com.example.androidtbc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.androidtbc.databinding.ItemTravelBinding
import javax.inject.Inject

class TravelAdapter @Inject constructor(
    private val glide: RequestManager
) : ListAdapter<ItemDTO, TravelAdapter.TravelViewHolder>(TravelDiffCallback()) {



    class TravelViewHolder(
        private val binding: ItemTravelBinding,
        private val glide: RequestManager
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemDTO) {
            with(binding) {
                glide.load(item.cover)
                    .centerCrop()
                    .into(ivImage)
                tvTitle.text = item.title
                tvLocation.text = item.location
                tvPrice.text = item.price
                tvReactionCount.text = item.reactionCount.toString()
                item.rate?.let { ratingBar.rating = it.toFloat() }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TravelViewHolder(
        ItemTravelBinding.inflate(LayoutInflater.from(parent.context), parent, false), glide
    )

    override fun onBindViewHolder(holder: TravelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class TravelDiffCallback : DiffUtil.ItemCallback<ItemDTO>() {
        override fun areItemsTheSame(oldItem: ItemDTO, newItem: ItemDTO) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ItemDTO, newItem: ItemDTO) = oldItem == newItem
    }
}