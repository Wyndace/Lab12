package com.example.lab12.di

import com.example.lab12.data.repository.NewsRepository
import com.example.lab12.data.repository.NewsRepositoryImpl
import com.example.lab12.data.repository.UserRepository
import com.example.lab12.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        newsRepositoryImpl: NewsRepositoryImpl
    ): NewsRepository
}
