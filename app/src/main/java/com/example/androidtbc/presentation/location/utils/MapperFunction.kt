package com.example.androidtbc.presentation.location.utils

import com.example.androidtbc.data.remote.dto.LocationDto
import com.example.androidtbc.presentation.location.model.LocationUi

fun LocationDto.toUi(): LocationUi {
    return LocationUi(
        latitude = latitude,
        longitude = longitude,
        title = title,
        address = address,
    )
}

fun List<LocationDto>.toUiList(): List<LocationUi> {
    return this.map { it.toUi() }
}