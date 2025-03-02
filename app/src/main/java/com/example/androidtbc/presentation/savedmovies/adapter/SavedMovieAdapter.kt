package com.example.androidtbc.presentation.savedmovies.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.databinding.ItemSavedMovieBinding
import com.example.androidtbc.presentation.model.MovieUI
import com.example.androidtbc.utils.loadTmdbImage

class SavedMovieAdapter(
    private val onMovieClicked: (MovieUI) -> Unit,
    private val onMovieLongClicked: (MovieUI) -> Boolean
) : ListAdapter<MovieUI, SavedMovieAdapter.SavedMovieViewHolder>(SavedMovieDiffCallback()) {

    inner class SavedMovieViewHolder(
        private val binding: ItemSavedMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieUI) {
            binding.apply {
                tvTitle.text = movie.title
                tvRating.text = String.format("%.1f", movie.voteAverage)
                tvGenre.text = movie.genreName
                tvYear.text = movie.releaseYear
                tvDuration.text = movie.runtime

                movie.posterPath?.let { posterPath ->
                    ivPoster.loadTmdbImage(posterPath)
                }

                root.setOnClickListener {
                    onMovieClicked(movie)
                }

                root.setOnLongClickListener {
                    onMovieLongClicked(movie)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedMovieViewHolder {
        return SavedMovieViewHolder(ItemSavedMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: SavedMovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
    }


    class SavedMovieDiffCallback : DiffUtil.ItemCallback<MovieUI>() {
        override fun areItemsTheSame(oldItem: MovieUI, newItem: MovieUI): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieUI, newItem: MovieUI): Boolean {
            return oldItem == newItem
        }
    }
}