package com.example.androidtbc.presentation.home.tablayoutfragments.upcoming.adapter

import com.example.androidtbc.databinding.ItemMoviePosterBinding
import com.example.androidtbc.presentation.base.BaseMovieAdapter
import com.example.androidtbc.presentation.model.Movie
import com.example.androidtbc.utils.loadTmdbImage

class UpcomingMovieAdapter(
    private val onMovieClick: (Movie) -> Unit
) : BaseMovieAdapter<ItemMoviePosterBinding>(
    bindingInflater = { inflater, parent, attachToParent ->
        ItemMoviePosterBinding.inflate(inflater, parent, attachToParent)
    },
    bindData = { binding, movie, position ->
        binding.ivPoster.loadTmdbImage(movie.posterPath, "w185")
    },
    itemClickListener = onMovieClick
)