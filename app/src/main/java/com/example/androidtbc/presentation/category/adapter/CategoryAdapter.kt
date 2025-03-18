package com.example.androidtbc.presentation.category.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.R
import com.example.androidtbc.databinding.ItemCategoryBinding
import com.example.androidtbc.presentation.model.CategoryPresentation


class CategoryAdapter : ListAdapter<CategoryPresentation, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: CategoryPresentation) {
            with(binding) {
                tvCategoryName.text = category.name

                depthIndicatorLayout.removeAllViews()

                if (category.depth > 0) {
                    val dotCount = minOf(category.depth, 4)

                    for (i in 0 until dotCount) {
                        val dotView = View(itemView.context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                itemView.context.resources.getDimensionPixelSize(R.dimen.dot_size),
                                itemView.context.resources.getDimensionPixelSize(R.dimen.dot_size)
                            ).also {
                                it.marginEnd = itemView.context.resources.getDimensionPixelSize(R.dimen.dot_margin)
                            }
                            background = ContextCompat.getDrawable(
                                itemView.context,
                                R.drawable.shape_orange_dot
                            )
                            visibility = View.VISIBLE
                        }
                        depthIndicatorLayout.addView(dotView)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryPresentation>() {
        override fun areItemsTheSame(oldItem: CategoryPresentation, newItem: CategoryPresentation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryPresentation, newItem: CategoryPresentation): Boolean {
            return oldItem == newItem
        }
    }
}