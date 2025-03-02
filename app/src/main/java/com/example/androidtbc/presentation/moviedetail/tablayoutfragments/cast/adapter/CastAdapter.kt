package com.example.androidtbc.presentation.moviedetail.tablayoutfragments.cast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.databinding.ItemCastBinding
import com.example.androidtbc.presentation.model.CastMember
import com.example.androidtbc.utils.loadTmdbImage

class CastAdapter(private val onCastClick: (CastMember) -> Unit) :
    ListAdapter<CastMember, CastAdapter.CastViewHolder>(CastDiffCallback()) {

    inner class CastViewHolder(private val binding: ItemCastBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cast: CastMember) {
            with(binding) {
                tvOriginalName.text = cast.name
                tvCharacter.text = cast.character
                ivCast.loadTmdbImage(cast.profilePath)

                root.setOnClickListener {
                    onCastClick(cast)
                }
            }
        }
    }

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

    class CastDiffCallback : DiffUtil.ItemCallback<CastMember>() {
        override fun areItemsTheSame(oldItem: CastMember, newItem: CastMember): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CastMember, newItem: CastMember): Boolean {
            return oldItem == newItem
        }
    }
}