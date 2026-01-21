package com.example.lab12.data.repository

import com.example.lab12.data.local.UserDao
import com.example.lab12.data.remote.UserApi
import com.example.lab12.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserRepository {
    fun getUsers(): Flow<List<User>>
    suspend fun refreshUsers()
}

class UserRepositoryImpl @Inject constructor(
    private val api: UserApi,
    private val dao: UserDao
) : UserRepository {

    override fun getUsers(): Flow<List<User>> {
        return dao.getUsers()
    }

    override suspend fun refreshUsers() {
        val remoteUsers = api.getUsers()
        dao.clearUsers()
        dao.insertUsers(remoteUsers.map { it.toUser() })
    }
}
