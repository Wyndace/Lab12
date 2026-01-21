package com.example.lab12.data.repository

import com.example.lab12.data.local.NewsDao
import com.example.lab12.data.remote.NewsApi
import com.example.lab12.domain.model.News
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface NewsRepository {
    fun getNews(): Flow<List<News>>
    suspend fun refreshNews()
}

class NewsRepositoryImpl @Inject constructor(
    private val api: NewsApi,
    private val dao: NewsDao
) : NewsRepository {

    override fun getNews(): Flow<List<News>> {
        return dao.getNews()
    }

    override suspend fun refreshNews() {
        val remoteNews = api.getNews()
        dao.clearNews()
        dao.insertNews(remoteNews.map { it.toNews() })
    }
}
