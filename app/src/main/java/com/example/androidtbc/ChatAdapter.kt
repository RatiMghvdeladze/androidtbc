package com.example.androidtbc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.databinding.ItemLeftBinding
import com.example.androidtbc.databinding.ItemRightBinding
import kotlinx.coroutines.flow.merge
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MessageDiffUtil : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }

}

class ChatListAdapter : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffUtil()) {

    companion object {
        private const val LEFT_MSG = 1
        private const val RIGHT_MSG = 2
    }


    inner class LeftViewHolder(private val binding: ItemLeftBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: Message) {
            binding.tvMessage.text = msg.messageText.trim()
            binding.tvTimestamp.text = formatTimestamp(msg.time)
        }

    }

    inner class RightViewHolder(private val binding: ItemRightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: Message) {
            binding.tvMessage.text = msg.messageText.trim()
            binding.tvTimestamp.text = formatTimestamp(msg.time)
        }

    }


    override fun getItemViewType(position: Int): Int {
        return if (currentList.size % 2 == 0) {
            if (position % 2 == 0) LEFT_MSG else RIGHT_MSG
        } else {
            if (position % 2 == 0) LEFT_MSG else RIGHT_MSG
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == LEFT_MSG)
            LeftViewHolder(
                ItemLeftBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        else RightViewHolder(
            ItemRightBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LeftViewHolder) {
            val message = getItem(position)
            holder.bind(message)
        } else if (holder is RightViewHolder) {
            val message = getItem(position)
            holder.bind(message)
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val currentTime = Calendar.getInstance()
        currentTime.timeInMillis = timestamp

        val pattern = StringBuilder()
        pattern.append("'Today', ")
        pattern.append("h:mm a")

        return SimpleDateFormat(pattern.toString(), Locale.getDefault())
            .format(Date(timestamp))
            .lowercase(Locale.getDefault())
    }

}
