package com.example.androidtbc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.databinding.UserItemBinding

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var users: List<UserEntity> = emptyList()

    fun setUsers(newUsers: List<UserEntity>) {
        users = newUsers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = users.size

    class UserViewHolder(private val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserEntity) {
            binding.tvUsername.text = user.name
            binding.tvUserStatus.text = getStatusText(user.activationStatus)

            val imageUrl = user.imageUrl ?: R.drawable.placeholder
//            binding.ivUserImage.load(imageUrl)
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
