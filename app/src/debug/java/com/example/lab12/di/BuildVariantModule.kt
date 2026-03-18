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
 * Модуль сетевого клиента для DEBUG-сборки.
 *
 * В debug-версии включено подробное логирование сетевых запросов и ответов
 * (включая тела запросов), что критически важно при разработке и отладке.
 *
 * Этот файл находится в src/debug/ и компилируется ТОЛЬКО в debug-сборке.
 * В release-сборке Hilt использует BuildVariantModule из src/release/.
 */
@Module
@InstallIn(SingletonComponent::class)
object BuildVariantModule {

    @Provides
    @Singleton
    fun provideDebugOkHttpClient(): OkHttpClient {
        // BODY — максимальный уровень логирования: выводит заголовки и тела запросов/ответов
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)   // Увеличенный таймаут для отладки
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                // В debug добавляем заголовок для обхода серверных ограничений на тестовом стенде
                val request = chain.request().newBuilder()
                    .addHeader("X-Debug-Mode", "true")
                    .build()
                chain.proceed(request)
            }
            .build()
    }
}
