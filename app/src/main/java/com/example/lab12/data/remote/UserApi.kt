package com.example.lab12.data.remote

import com.example.lab12.data.remote.dto.UserDto
import retrofit2.http.GET

interface UserApi {
    @GET("users")
    suspend fun getUsers(): List<UserDto>
}
