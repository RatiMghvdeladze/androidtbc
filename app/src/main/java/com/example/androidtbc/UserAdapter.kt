package com.example.androidtbc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class UserAdapter : ListAdapter<User, UserViewHolder>(UserDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class UserViewHolder(
    private val binding: ItemUserBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: User) {
        binding.apply {
            userName.text = user.name

            // Load avatar with placeholder and error handling
            Glide.with(itemView)
                .load(user.avatar)
                .placeholder(R.drawable.placeholder_avatar)
                .error(R.drawable.error_avatar)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(userAvatar)

            val (statusText, statusColor) = when {
                user.activationStatus <= 0 -> "Inactive" to R.color.status_inactive
                user.activationStatus == 1 -> "Online" to R.color.status_online
                user.activationStatus == 2 -> "Recently active" to R.color.status_recently
                user.activationStatus in 3..22 -> "Hours ago" to R.color.status_hours
                else -> "Long time ago" to R.color.status_longtime
            }

            statusIndicator.text = statusText
            statusIndicator.setTextColor(ContextCompat.getColor(itemView.context, statusColor))
        }
    }
}