package com.example.androidtbc.presentation.home.adapter

import com.example.androidtbc.data.remote.dto.MovieResult
import com.example.androidtbc.databinding.ItemMoviePosterBinding
import com.example.androidtbc.presentation.base.BaseMovieAdapter
import com.example.androidtbc.utils.loadTmdbImage

class PopularMoviesSearchAdapter(
    private val onMovieClick: (MovieResult) -> Unit
) : BaseMovieAdapter<ItemMoviePosterBinding>(
    bindingInflater = { inflater, parent, attachToParent ->
        ItemMoviePosterBinding.inflate(inflater, parent, attachToParent)
    },
    bindData = { binding, movie, _ ->
        binding.ivPoster.loadTmdbImage(movie.posterPath)

        binding.root.setOnClickListener {
        }
    },
    itemClickListener = onMovieClick
)