package com.example.lab12.data.remote.dto

import com.example.lab12.domain.model.User

data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val username: String
) {
    fun toUser(): User {
        return User(
            id = id,
            name = name,
            email = email,
            avatarUrl = "https://ui-avatars.com/api/?name=$name&background=random"
        )
    }
}
