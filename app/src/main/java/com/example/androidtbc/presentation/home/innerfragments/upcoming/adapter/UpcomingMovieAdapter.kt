package com.example.androidtbc.presentation.home.innerfragments.upcoming.adapter

import com.bumptech.glide.Glide
import com.example.androidtbc.databinding.ItemMoviePosterBinding
import com.example.androidtbc.presentation.base.BaseMovieAdapter

class UpcomingMovieAdapter : BaseMovieAdapter<ItemMoviePosterBinding>(
    bindingInflater = { inflater, parent, attachToParent ->
        ItemMoviePosterBinding.inflate(inflater, parent, attachToParent)
    },
    bindData = { binding, movie, position ->
        val posterUrl = "https://image.tmdb.org/t/p/w185${movie.posterPath}"
        Glide.with(binding.root.context)
            .load(posterUrl)
            .centerCrop()
            .into(binding.imageViewPoster)
    }
)