package com.example.androidtbc.presentation.imageaction

import com.example.androidtbc.R

enum class ImageAction(
    val icon: Int,
    val title: Int
) {
    GALLERY(icon = R.drawable.ic_gallery, title = R.string.choose_from_gallery),
    CAMERA(icon = R.drawable.ic_camera, title = R.string.capture_new_photo),
}