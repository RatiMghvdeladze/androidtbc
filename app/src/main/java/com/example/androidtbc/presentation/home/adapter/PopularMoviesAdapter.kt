package com.example.androidtbc.presentation.home.adapter

import com.example.androidtbc.databinding.ItemPopularMoviesBinding
import com.example.androidtbc.presentation.base.BaseMovieAdapter
import com.example.androidtbc.presentation.model.Movie
import com.example.androidtbc.utils.loadTmdbImage

class PopularMoviesAdapter(
    private val onMovieClick: (Movie) -> Unit
) : BaseMovieAdapter<ItemPopularMoviesBinding>(
    bindingInflater = { inflater, parent, attachToParent ->
        ItemPopularMoviesBinding.inflate(inflater, parent, attachToParent)
    },
    bindData = { binding, movie, position ->
        with(binding) {
            binding.ivPoster.loadTmdbImage(movie.posterPath)
            tvNumber.text = (position + 1).toString()
        }
    },
    itemClickListener = onMovieClick
)