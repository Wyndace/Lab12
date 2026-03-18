package com.example.lab12.network

import android.util.Log
import com.example.lab12.data.remote.UserApi
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Демонстрация использования AssistedInject фабрики.
 *
 * Этот класс показывает, как разные компоненты приложения могут получать
 * ApiClient с разными baseUrl, используя одну и ту же фабрику из Hilt-графа.
 */
@Singleton
class ApiClientDemo @Inject constructor(
    private val apiClientFactory: ApiClient.Factory
) {

    /**
     * Создаёт клиент для основного API приложения.
     */
    fun createMainApiClient(): ApiClient {
        return apiClientFactory.create(
            baseUrl = "https://jsonplaceholder.typicode.com/",
            timeoutSeconds = 30L
        )
    }

    /**
     * Создаёт клиент для внешнего сервиса с другим URL и коротким таймаутом.
     */
    fun createExternalServiceClient(serverUrl: String): ApiClient {
        return apiClientFactory.create(
            baseUrl = serverUrl,
            timeoutSeconds = 10L
        )
    }

    /**
     * Демонстрирует создание UserApi через динамически созданный клиент.
     */
    fun createUserApi(baseUrl: String): UserApi {
        val client = apiClientFactory.create(baseUrl, 30L)
        Log.d(TAG, "UserApi создан для $baseUrl")
        return client.createService(UserApi::class.java)
    }

    companion object {
        private const val TAG = "ApiClientDemo"
    }
}
