package com.example.androidtbc.presentation.home.innerfragments.toprated.adapter

import com.example.androidtbc.data.remote.dto.MovieResult
import com.example.androidtbc.databinding.ItemMoviePosterBinding
import com.example.androidtbc.presentation.base.BaseMovieAdapter
import com.example.androidtbc.utils.loadTmdbImage

class TopRatedMovieAdapter(
    private val onMovieClick: (MovieResult) -> Unit // Change Result to MovieResult
) : BaseMovieAdapter<ItemMoviePosterBinding>(
    bindingInflater = { inflater, parent, attachToParent ->
        ItemMoviePosterBinding.inflate(inflater, parent, attachToParent)
    },
    bindData = { binding, movie, position ->
        binding.ivPoster.loadTmdbImage(movie.posterPath, "w185")
    },
    itemClickListener = onMovieClick
)