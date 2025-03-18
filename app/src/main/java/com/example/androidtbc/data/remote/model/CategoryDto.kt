package com.example.androidtbc.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("name_de") val nameDe: String,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("bgl_number") val bglNumber: String? = null,
    @SerialName("bgl_variant") val bglVariant: String? = null,
    @SerialName("order_id") val orderId: Int?,
    @SerialName("main") val main: String? = null,
    @SerialName("children") val children: List<CategoryDto> = emptyList()
)
