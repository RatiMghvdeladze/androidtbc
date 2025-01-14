package com.example.androidtbc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.databinding.ItemCardBinding


class CardDiffUtilBack : DiffUtil.ItemCallback<Card>() {
    override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
        return oldItem.cardNumber == newItem.id
    }

    override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
        return oldItem == newItem
    }
}

class CardAdapter : ListAdapter<Card, CardAdapter.CardViewHolder>(CardDiffUtilBack()) {
    private var onLongClickListener: ((Card) -> Unit)? = null

    fun longClickListener(listener: (Card) -> Unit) {
        onLongClickListener = listener
    }

    inner class CardViewHolder(private val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(card: Card) {
            val formattedNumber = card.cardNumber.chunked(4).joinToString(" ")

            binding.tvCardNumber.text = formattedNumber
            binding.tvCardHolderName.text = card.name
            binding.tvValidThru.text = card.validThru

            println(card)

            when (card.type) {
                CardType.VISA -> {
                    binding.ivVisaOrMastercard.setImageResource(R.drawable.visa)
                    binding.root.setBackgroundResource(R.drawable.bg_card_visa)


                }

                CardType.MASTERCARD -> {
                    binding.ivVisaOrMastercard.setImageResource(R.drawable.mastercard)
                    binding.root.setBackgroundResource(R.drawable.bg_card_mastercard)
                }
            }
            itemView.setOnLongClickListener {
                onLongClickListener?.invoke(card)
                true
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(
            ItemCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}