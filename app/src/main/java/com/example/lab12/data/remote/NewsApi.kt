package com.example.lab12.data.remote

import com.example.lab12.data.remote.dto.NewsDto
import retrofit2.http.GET

interface NewsApi {
    @GET("posts")
    suspend fun getNews(): List<NewsDto>
}
