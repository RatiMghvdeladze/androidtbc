package com.example.androidtbc.presentation.extension

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar


fun View.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    backgroundColorResId: Int? = null,
    textColorResId: Int? = null
) {
    val context = this.context
    Snackbar.make(this, message, duration).apply {
        // Apply background color if provided
        backgroundColorResId?.let {
            setBackgroundTint(ContextCompat.getColor(context, it))
        }

        // Apply text color if provided
        textColorResId?.let {
            setTextColor(ContextCompat.getColor(context, it))
        }

        show()
    }
}