package com.example.androidtbc.presentation.savedmovies.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtbc.data.remote.dto.MovieDetailDto
import com.example.androidtbc.databinding.ItemSavedMovieBinding
import com.example.androidtbc.utils.loadTmdbImage

class SavedMovieAdapter(
    private val onMovieClicked: (MovieDetailDto) -> Unit
) : ListAdapter<MovieDetailDto, SavedMovieAdapter.SavedMovieViewHolder>(SavedMovieDiffCallback()) {


    inner class SavedMovieViewHolder(
        private val binding: ItemSavedMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMovieClicked(getItem(position))
                }
            }
        }

        fun bind(movie: MovieDetailDto) {
            binding.apply {
                tvTitle.text = movie.title

                tvRating.text = movie.voteAverage.toString()

                val genreName = movie.genres?.firstOrNull()?.name ?: "N/A"
                tvGenre.text = genreName

                tvYear.text = movie.releaseDate.split("-").firstOrNull() ?: ""

                val runtime = movie.runtime ?: 0
                tvDuration.text = "$runtime minutes"

                movie.posterPath?.let { posterPath ->
                    ivPoster.loadTmdbImage(posterPath)
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


    class SavedMovieDiffCallback : DiffUtil.ItemCallback<MovieDetailDto>() {
        override fun areItemsTheSame(oldItem: MovieDetailDto, newItem: MovieDetailDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieDetailDto, newItem: MovieDetailDto): Boolean {
            return oldItem == newItem
        }
    }
}