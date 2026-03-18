package com.example.lab12.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Модуль сетевого клиента для RELEASE-сборки.
 *
 * В production-версии приоритет отдаётся:
 * - Кэшированию ответов (снижение нагрузки на сеть и увеличение скорости)
 * - Минимальному логированию (только статус ответа, без тела — нельзя логировать данные пользователей)
 * - Повторным попыткам при потере соединения
 *
 * Этот файл находится в src/release/ и компилируется ТОЛЬКО в release-сборке.
 */
@Module
@InstallIn(SingletonComponent::class)
object BuildVariantModule {

    private const val CACHE_SIZE_BYTES = 10L * 1024 * 1024  // 10 МБ

    @Provides
    @Singleton
    fun provideReleaseOkHttpClient(): OkHttpClient {
        // NONE — логирование отключено в production (безопасность и производительность)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addNetworkInterceptor { chain ->
                // Интерцептор кэширования: форсируем кэш на 5 минут для GET-запросов
                val request = chain.request()
                val response = chain.proceed(request)

                if (request.method == "GET") {
                    val cacheControl = CacheControl.Builder()
                        .maxAge(5, TimeUnit.MINUTES)
                        .build()
                    response.newBuilder()
                        .header("Cache-Control", cacheControl.toString())
                        .build()
                } else {
                    response
                }
            }
            .retryOnConnectionFailure(true)
            .build()
    }
}
