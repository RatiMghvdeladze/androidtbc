package com.example.androidtbc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidtbc.databinding.ItemLayoutBinding

class FieldDiffCallBack : DiffUtil.ItemCallback<FieldDTO>(){
    override fun areItemsTheSame(oldItem: FieldDTO, newItem: FieldDTO): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FieldDTO, newItem: FieldDTO): Boolean {
        return oldItem == newItem
    }

}

class FieldsAdapter : ListAdapter<FieldDTO, FieldsAdapter.FieldViewHolder>(FieldDiffCallBack()) {

    inner class FieldViewHolder(private val binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FieldDTO) {
            with(binding) {
                tvName.text = item.owner
                tvMessage.text = item.lastMessage
                tvTime.text = item.lastActive

                if (item.unreadMessages > 0) {
                    tvUnreadMessages.visibility = View.VISIBLE
                    tvUnreadMessages.text = item.unreadMessages.toString()
                } else {
                    tvUnreadMessages.visibility = View.GONE
                }

                val messageIcon = when (item.lastMessageType.lowercase()) {
                    "file" -> R.drawable.ic_attachment
                    "voice" -> R.drawable.ic_voice
                    "text" -> 0
                    else -> 0
                }

                if (messageIcon != 0) {
                    tvMessage.setCompoundDrawablesWithIntrinsicBounds(messageIcon, 0, 0, 0)
                } else {
                    tvMessage.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }


                Glide.with(itemView.context)
                    .load(item.image)
                    .placeholder(R.drawable.picture)
                    .error(R.drawable.picture)
                    .circleCrop()
                    .into(binding.ivProfile)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldViewHolder {
        return FieldViewHolder(
            ItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FieldViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
