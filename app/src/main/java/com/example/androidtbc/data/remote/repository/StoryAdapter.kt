package com.example.androidtbc.data.remote.repository

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.androidtbc.R
import com.example.androidtbc.data.remote.dto.StoryDTO
import com.example.androidtbc.databinding.ItemStoryBinding

class StoryAdapter(
    private val onStoryClick: (StoryDTO) -> Unit = {}
) : ListAdapter<StoryDTO, StoryAdapter.StoryViewHolder>(StoryDiffUtil()) {

    inner class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoryDTO) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(item.cover)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.img)
                    .error(R.drawable.img)
                    .centerCrop()
                    .into(ivStory)

                tvTitle.text = item.title.takeIf { it.isNotBlank() } ?: "Untitled Story"

                root.setOnClickListener { onStoryClick(item) }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        return StoryViewHolder(
            ItemStoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class StoryDiffUtil : DiffUtil.ItemCallback<StoryDTO>() {
    override fun areItemsTheSame(oldItem: StoryDTO, newItem: StoryDTO): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StoryDTO, newItem: StoryDTO): Boolean {
        return oldItem == newItem
    }
}