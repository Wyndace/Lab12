package com.example.lab12.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lab12.domain.model.News
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM news")
    fun getNews(): Flow<List<News>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<News>)

    @Query("DELETE FROM news")
    suspend fun clearNews()
}
