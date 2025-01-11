package com.example.androidtbc

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat


fun updateColorCircle(context: Context, color: String): GradientDrawable {
    val drawable = GradientDrawable()
    drawable.shape = GradientDrawable.OVAL

    val colorId = when (color.lowercase()) {
        "brown" -> R.color.brown
        "black" -> R.color.black
        "blue grey" -> R.color.blue_grey
        else -> R.color.black
    }
    drawable.setColor(ContextCompat.getColor(context, colorId))
    return drawable
}