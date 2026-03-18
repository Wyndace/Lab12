package com.example.lab12.session

import javax.inject.Scope

/**
 * Кастомная область видимости для объектов, живущих пока пользователь авторизован.
 * Используется в паре с UserSessionComponent.
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class UserSessionScoped
