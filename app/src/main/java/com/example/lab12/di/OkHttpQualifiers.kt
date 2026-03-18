package com.example.lab12.di

import javax.inject.Qualifier

/**
 * Квалификатор для OkHttpClient основного API.
 * Используется при внедрении клиента для работы с основными эндпоинтами приложения.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainApiClient

/**
 * Квалификатор для OkHttpClient аналитического сервиса.
 * Используется при внедрении клиента для отправки аналитических событий.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AnalyticsClient
