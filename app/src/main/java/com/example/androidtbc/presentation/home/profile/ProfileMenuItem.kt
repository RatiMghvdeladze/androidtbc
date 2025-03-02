package com.example.androidtbc.presentation.home.profile

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class  ProfileMenuItem(
    val id: Int,
    @DrawableRes val iconResId: Int,
    @DrawableRes val backgroundResId: Int,
    @ColorRes val iconTint: Int? = null,
    @StringRes val titleResId: Int,
    @StringRes val descriptionResId: Int? = null,
    val showDivider: Boolean = true
)