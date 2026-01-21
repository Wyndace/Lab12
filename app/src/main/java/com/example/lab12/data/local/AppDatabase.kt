package com.example.lab12.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.lab12.domain.model.News
import com.example.lab12.domain.model.User

@Database(entities = [User::class, News::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun newsDao(): NewsDao
}
