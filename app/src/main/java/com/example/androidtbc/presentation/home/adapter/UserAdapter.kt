package com.example.androidtbc.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidtbc.R
import com.example.androidtbc.databinding.ItemUserBinding
import com.example.androidtbc.domain.models.UserDomain

class UsersAdapter: PagingDataAdapter<UserDomain, UsersAdapter.UserViewHolder>(USER_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserDomain) {
            with(binding) {
                tvEmail.text = user.email
                tvUsername.text = "${user.firstName} ${user.lastName}"

                Glide.with(root)
                    .load(user.avatar)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(ivAvatar)
            }
        }
    }

    companion object {
        private val USER_COMPARATOR = object : DiffUtil.ItemCallback<UserDomain>() {
            override fun areItemsTheSame(oldItem: UserDomain, newItem: UserDomain): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserDomain, newItem: UserDomain): Boolean {
                return oldItem == newItem
            }
        }
    }
}