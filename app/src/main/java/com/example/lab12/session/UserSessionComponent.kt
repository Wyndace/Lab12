package com.example.lab12.session

import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent

/**
 * Кастомный Hilt-компонент для сессии пользователя.
 * Является дочерним по отношению к SingletonComponent.
 * Живёт ровно столько, сколько существует сессия пользователя.
 */
@UserSessionScoped
@DefineComponent(parent = SingletonComponent::class)
interface UserSessionComponent
