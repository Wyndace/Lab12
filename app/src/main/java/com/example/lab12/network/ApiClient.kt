package com.example.lab12.network

import android.util.Log
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Клиент для работы с API с динамически задаваемым baseUrl.
 *
 * @AssistedInject позволяет комбинировать:
 *   - зависимости из Hilt-графа (okHttpClient) — внедряются автоматически
 *   - динамические параметры времени выполнения (baseUrl, timeout) — передаются через фабрику
 *
 * Паттерн полезен, когда baseUrl становится известен только во время выполнения
 * (например, выбирается пользователем или приходит с сервера конфигурации).
 */
class ApiClient @AssistedInject constructor(
    // Зависимость из Hilt-графа: внедряется автоматически
    private val okHttpClient: OkHttpClient,
    // Динамический параметр: передаётся через AssistedFactory при создании
    @Assisted val baseUrl: String,
    // Ещё один динамический параметр с именем для различения
    @Assisted("timeout") val timeoutSeconds: Long
) {

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> createService(serviceClass: Class<T>): T {
        Log.d(TAG, "Создание сервиса ${serviceClass.simpleName} для $baseUrl")
        return retrofit.create(serviceClass)
    }

    companion object {
        private const val TAG = "ApiClient"
    }

    /**
     * Фабрика для создания ApiClient с произвольным baseUrl и таймаутом.
     * Hilt автоматически генерирует реализацию этого интерфейса.
     *
     * Использование:
     *   val client = apiClientFactory.create("https://api.example.com/", 30L)
     */
    @AssistedFactory
    interface Factory {
        fun create(
            baseUrl: String,
            @Assisted("timeout") timeoutSeconds: Long
        ): ApiClient
    }
}
