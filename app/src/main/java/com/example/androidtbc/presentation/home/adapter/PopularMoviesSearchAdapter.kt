package com.example.androidtbc.presentation.home.adapter

import com.bumptech.glide.Glide
import com.example.androidtbc.databinding.ItemMoviePosterBinding
import com.example.androidtbc.presentation.base.BaseMovieAdapter

class PopularMoviesSearchAdapter : BaseMovieAdapter<ItemMoviePosterBinding>(
    bindingInflater = { inflater, parent, attachToParent ->
        ItemMoviePosterBinding.inflate(inflater, parent, attachToParent)
    },
    bindData = { binding, movie, _ ->
        Glide.with(binding.root.context)
            .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
            .into(binding.imageViewPoster)

        binding.root.setOnClickListener {
        }
    }
)