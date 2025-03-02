package com.example.androidtbc.presentation.home.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.databinding.ItemProfileMenuBinding
import com.example.androidtbc.presentation.home.profile.ProfileMenuItem

class ProfileItemAdapter(private val onItemClicked: (ProfileMenuItem) -> Unit) :
    ListAdapter<ProfileMenuItem, ProfileItemAdapter.MenuItemViewHolder>(ProfileItemDiffCallback()) {

    inner class MenuItemViewHolder(private val binding: ItemProfileMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProfileMenuItem) {
            with(binding) {
                with(item) {
                    root.setOnClickListener {
                        val position = bindingAdapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClicked(getItem(position))
                        }
                    }

                    ivIcon.setImageResource(iconResId)
                    ivIcon.background = ivIcon.context.getDrawable(backgroundResId)
                    if (iconTint != null) {
                        ivIcon.setColorFilter(ivIcon.context.getColor(iconTint))
                    } else {
                        ivIcon.clearColorFilter()
                    }
                    tvTitle.text = itemView.context.getString(titleResId)

                    if (descriptionResId != null) {
                        tvDescription.text = itemView.context.getString(descriptionResId)
                        tvDescription.visibility = View.VISIBLE
                    } else {
                        tvDescription.visibility = View.GONE
                    }
                    divider.visibility = if (showDivider) View.VISIBLE else View.GONE
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val binding = ItemProfileMenuBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

class ProfileItemDiffCallback : DiffUtil.ItemCallback<ProfileMenuItem>() {
    override fun areItemsTheSame(oldItem: ProfileMenuItem, newItem: ProfileMenuItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProfileMenuItem, newItem: ProfileMenuItem): Boolean {
        return oldItem == newItem
    }
}