package com.example.androidtbc.presentation.moviedetail.innerfragments.cast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.data.remote.dto.CastMemberDto
import com.example.androidtbc.databinding.ItemCastBinding
import com.example.androidtbc.utils.loadTmdbImage

class CastAdapter(private val onCastClick: (CastMemberDto) -> Unit) :
    ListAdapter<CastMemberDto, CastAdapter.CastViewHolder>(CastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val binding = ItemCastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val cast = getItem(position)
        holder.bind(cast)
    }

    inner class CastViewHolder(private val binding: ItemCastBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCastClick(getItem(position))
                }
            }
        }

        fun bind(cast: CastMemberDto) {
            binding.apply {
                tvOriginalName.text = cast.name
                tvCharacter.text = cast.character

                // Load image if profilePath is not null
                ivCast.loadTmdbImage(cast.profilePath)
            }
        }
    }

    class CastDiffCallback : DiffUtil.ItemCallback<CastMemberDto>() {
        override fun areItemsTheSame(oldItem: CastMemberDto, newItem: CastMemberDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CastMemberDto, newItem: CastMemberDto): Boolean {
            return oldItem == newItem
        }
    }
}