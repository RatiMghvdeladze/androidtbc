package com.example.androidtbc.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.androidtbc.R

fun ImageView.loadTmdbImage(
    path: String?,
    size: String = "w500",
    centerCrop: Boolean = false
) {
    val fullUrl = path?.let { "https://image.tmdb.org/t/p/$size$it" }

    val requestOptions = RequestOptions()
        .placeholder(R.drawable.ic_unknown_person)
        .error(R.drawable.ic_unknown_person)
        .apply { if (centerCrop) centerCrop() }

    Glide.with(this.context)
        .load(fullUrl)
        .apply(requestOptions)
        .into(this)
}
