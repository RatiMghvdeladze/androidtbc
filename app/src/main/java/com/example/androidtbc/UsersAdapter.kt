package com.example.androidtbc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidtbc.databinding.ItemUserBinding


class UsersDiffUtill : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return  oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}

class UsersAdapter: ListAdapter<User, UsersAdapter.UserViewHolder>(UsersDiffUtill()) {
    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(){
            val item = getItem(adapterPosition)
            with(binding){

                tvEmail.text = item.email
                tvUsername.text = "${item.firstName} ${item.lastName}"

                Glide.with(root).load(item.avatar).placeholder(R.drawable.ic_person).error(R.drawable.ic_person).into(ivAvatar)


            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
         return UserViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind()
    }
}