package com.example.androidtbc

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class MessageTypeAdapter {
    @FromJson
    fun fromJson(value: String): MessageType {
        return when (value.lowercase()) {
            "file" -> MessageType.FILE
            "voice" -> MessageType.VOICE
            else -> MessageType.TEXT
        }
    }

    @ToJson
    fun toJson(type: MessageType): String {
        return type.name.lowercase()
    }
}
