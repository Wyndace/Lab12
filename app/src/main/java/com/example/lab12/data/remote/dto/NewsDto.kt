package com.example.lab12.data.remote.dto

import com.example.lab12.domain.model.News

data class NewsDto(
    val id: Int,
    val title: String,
    val body: String
) {
    fun toNews(): News {
        return News(
            id = id,
            title = title,
            description = body,
            imageUrl = "https://picsum.photos/seed/$id/300/200"
        )
    }
}
