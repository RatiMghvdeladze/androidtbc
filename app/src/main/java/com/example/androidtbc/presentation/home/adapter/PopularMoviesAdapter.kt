package com.example.androidtbc.presentation.home.adapter

import com.bumptech.glide.Glide
import com.example.androidtbc.databinding.ItemPopularMoviesBinding
import com.example.androidtbc.presentation.base.BaseMovieAdapter

class PopularMoviesAdapter : BaseMovieAdapter<ItemPopularMoviesBinding>(
    bindingInflater = { inflater, parent, attachToParent ->
        ItemPopularMoviesBinding.inflate(inflater, parent, attachToParent)
    },
    bindData = { binding, movie, position ->
        with(binding) {
            Glide.with(root.context)
                .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
                .centerCrop()
                .into(imageViewPoster)

            textViewTitle.text = movie.title
            textViewNumber.text = (position + 1).toString()
        }
    }
)
