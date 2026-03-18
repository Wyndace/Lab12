package com.example.lab12.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt-модуль для предоставления двух различных OkHttpClient.
 *
 * Задача: разделить сетевые клиенты для основного API и аналитики,
 * применив разные настройки (таймауты, интерцепторы, заголовки).
 *
 * @Qualifier (@MainApiClient / @AnalyticsClient) позволяет Hilt понять,
 * какой именно экземпляр OkHttpClient нужно внедрить в конкретное место.
 */
@Module
@InstallIn(SingletonComponent::class)
object OkHttpModule {

    /**
     * OkHttpClient для основного API.
     *
     * Характеристики:
     * - Увеличенные таймауты (подходит для тяжёлых запросов)
     * - Логирование только заголовков (не тело, для экономии памяти)
     * - Интерцептор авторизации (добавляет заголовок Authorization)
     */
    @Provides
    @Singleton
    @MainApiClient
    fun provideMainApiOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                // Интерцептор авторизации: добавляет Bearer-токен к каждому запросу
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer <token>")
                    .addHeader("X-App-Version", "1.0.0")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    /**
     * OkHttpClient для аналитического сервиса.
     *
     * Характеристики:
     * - Короткие таймауты (аналитика не должна блокировать UI)
     * - Минимальное логирование (только статус ответа)
     * - Интерцептор с ключом API аналитики
     * - Fire-and-forget: не ждём полного тела ответа
     */
    @Provides
    @Singleton
    @AnalyticsClient
    fun provideAnalyticsOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                // Интерцептор аналитики: добавляет ключ API и идентификатор платформы
                val request = chain.request().newBuilder()
                    .addHeader("X-Analytics-Key", "<analytics-api-key>")
                    .addHeader("X-Platform", "Android")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    /**
     * Предоставляем "дефолтный" OkHttpClient (без квалификатора) для обратной совместимости.
     * Делегирует к основному API-клиенту.
     */
    @Provides
    @Singleton
    fun provideDefaultOkHttpClient(@MainApiClient client: OkHttpClient): OkHttpClient = client
}
