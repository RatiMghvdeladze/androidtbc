package com.example.androidtbc.presentation.home.innerfragments.upcoming.adapter

import com.example.androidtbc.data.remote.dto.MovieResult
import com.example.androidtbc.databinding.ItemMoviePosterBinding
import com.example.androidtbc.presentation.base.BaseMovieAdapter
import com.example.androidtbc.utils.loadTmdbImage

class UpcomingMovieAdapter(
    private val onMovieClick: (MovieResult) -> Unit
) : BaseMovieAdapter<ItemMoviePosterBinding>(
    bindingInflater = { inflater, parent, attachToParent ->
        ItemMoviePosterBinding.inflate(inflater, parent, attachToParent)
    },
    bindData = { binding, movie, position ->
        binding.ivPoster.loadTmdbImage(movie.posterPath, "w185")
    },
    itemClickListener = onMovieClick
)