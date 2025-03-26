package com.example.androidtbc.presentation.imageaction.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.databinding.ItemImageActionBinding
import com.example.androidtbc.presentation.extension.loadImage
import com.example.androidtbc.presentation.imageaction.ImageAction

class ImageActionAdapter(private val onClick: (String) -> Unit) : RecyclerView.Adapter<ImageActionAdapter.ImageActionViewHolder>() {
    private val items = ImageAction.entries.toList()

    inner class ImageActionViewHolder(private val binding: ItemImageActionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val item = items[bindingAdapterPosition]
            with(binding) {
                tvImageAction.text = root.context.getString(item.title)
                ivImageAction.loadImage(item.icon)
                root.setOnClickListener {
                    onClick(item.name)
                }
            }
        }
    }
    override fun onBindViewHolder(holder: ImageActionViewHolder, position: Int) {
        holder.bind()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageActionViewHolder {
        return ImageActionViewHolder(ItemImageActionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = items.size
}