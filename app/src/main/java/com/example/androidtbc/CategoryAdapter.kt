package com.example.androidtbc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.databinding.ItemCategoryBinding

class CategoryAdapter(private val categories: List<CategoryType>)
    : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {


        private var listener1: ((CategoryType) -> Unit)? = null

    fun onClick(listener: (CategoryType) -> Unit){
        this.listener1 = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: CategoryViewHolder, position: Int) {
        viewHolder.bind(categories[position])
    }

    override fun getItemCount() = categories.size

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding,

        ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryType) {
            binding.tvCategoryName.text = category.name
            binding.root.setOnClickListener {
                listener1?.invoke(category)
            }
        }
    }
}
