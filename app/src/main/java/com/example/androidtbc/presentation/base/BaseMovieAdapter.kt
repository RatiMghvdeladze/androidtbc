package com.example.androidtbc.presentation.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.androidtbc.presentation.model.Movie

/**
 * A generic movie adapter that can be used for different movie lists
 * @param bindingInflater Function to inflate the appropriate binding
 * @param bindData Function to bind data to the view
 */
abstract class BaseMovieAdapter<VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
    private val bindData: (VB, Movie, Int) -> Unit,
    private val itemClickListener: ((Movie) -> Unit)? = null
) : PagingDataAdapter<Movie, BaseMovieAdapter.MovieViewHolder<VB>>(MovieDiffCallback()) {

    class MovieViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder<VB> {
        val binding = bindingInflater(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder<VB>, position: Int) {
        getItem(position)?.let { movie ->
            bindData(holder.binding, movie, position)

            holder.binding.root.setOnClickListener {
                itemClickListener?.invoke(movie)
            }
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }
}