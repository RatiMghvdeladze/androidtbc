package com.example.androidtbc

import com.squareup.moshi.Json

enum class MessageType {
    @Json(name = "text")
    TEXT,
    @Json(name = "file")
    FILE,
    @Json(name = "voice")
    VOICE
}