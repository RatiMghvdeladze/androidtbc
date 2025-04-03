package com.example.androidtbc.presentation.mapper

import com.example.androidtbc.domain.models.UserDomain
import com.example.androidtbc.presentation.models.UserPresentation

fun UserDomain.toUserPresentation(): UserPresentation {
    return UserPresentation(
        id = id,
        email = email,
        fullName = "$firstName $lastName",
        avatar = avatar
    )
}