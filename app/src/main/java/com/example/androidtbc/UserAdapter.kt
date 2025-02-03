package com.example.androidtbc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.databinding.UserItemBinding

class UserAdapter : ListAdapter<UserEntity, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    class UserViewHolder(private val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserEntity) {
            binding.tvUsername.text = user.name
            binding.tvUserStatus.text = getStatusText(user.activationStatus)

//            val imageUrl = user.imageUrl
//            binding.ivUserImage.load(imageUrl ?: R.drawable.placeholder) {
//                placeholder(R.drawable.placeholder)
//                error(R.drawable.placeholder)
//            }
        }

        private fun getStatusText(status: Int): String {
            return when {
                status <= 0 -> "Not Active"
                status == 1 -> "Online"
                status == 2 -> "Active a few minutes ago"
                status in 3..22 -> "Active hours ago"
                else -> "Inactive"
            }
        }
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<UserEntity>() {
    override fun areItemsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
        return oldItem == newItem
    }
}

