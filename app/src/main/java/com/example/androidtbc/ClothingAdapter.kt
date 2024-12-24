package com.example.androidtbc
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.databinding.ItemClothingBinding

class ClothingAdapter : RecyclerView.Adapter<ClothingAdapter.ClothingViewHolder>() {

    private var items = listOf<Item>()

    inner class ClothingViewHolder(private val binding: ItemClothingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.ivItemImage.setImageResource(item.imageResId)
            binding.tvItemTitle.text = item.title
            binding.tvItemPrice.text = "$${item.price}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothingViewHolder {
        val binding = ItemClothingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClothingViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ClothingViewHolder, position: Int) {
        holder.bind(items[position])
        println("bindViewHolder")
    }



    override fun getItemCount() = items.size

    fun updateList(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }
}
