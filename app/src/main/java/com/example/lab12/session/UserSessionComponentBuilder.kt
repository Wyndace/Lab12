package com.example.lab12.session

import dagger.hilt.DefineComponent

/**
 * Builder для создания UserSessionComponent.
 * Hilt генерирует реализацию этого интерфейса автоматически.
 */
@DefineComponent.Builder
interface UserSessionComponentBuilder {
    fun build(): UserSessionComponent
}
