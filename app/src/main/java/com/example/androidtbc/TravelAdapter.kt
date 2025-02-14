package com.example.androidtbc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.androidtbc.data.remote.dto.ItemDTO
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

                val stars = listOf(star1, star2, star3, star4, star5)
                item.rate?.let { rating ->
                    stars.forEachIndexed { index, starView ->
                        val isFilled = index < rating
                        starView.setImageResource(
                            if (isFilled) R.drawable.ic_star
                            else R.drawable.ic_star_outline
                        )
                    }
                }
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
        override fun areItemsTheSame(oldItem: ItemDTO, newItem: ItemDTO) : Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: ItemDTO, newItem: ItemDTO) : Boolean {
            return oldItem == newItem
        }
    }
}