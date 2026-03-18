package com.example.lab12.session

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Менеджер пользовательской сессии.
 *
 * Управляет жизненным циклом UserSessionComponent:
 * создаёт компонент при входе пользователя и уничтожает при выходе.
 * Сам является @Singleton — существует на протяжении всей жизни приложения,
 * тогда как управляемый им компонент пересоздаётся при каждом входе.
 */
@Singleton
class UserSessionManager @Inject constructor(
    private val componentBuilder: UserSessionComponentBuilder
) {

    private var userSessionComponent: UserSessionComponent? = null

    val isLoggedIn: Boolean
        get() = userSessionComponent != null

    /**
     * Вызывается при успешной авторизации пользователя.
     * Создаёт новый UserSessionComponent, инициализируя сессионные зависимости.
     */
    fun login(userId: String) {
        if (userSessionComponent != null) {
            Log.w(TAG, "login() вызван при уже активной сессии — сессия перезапускается")
            logout()
        }
        userSessionComponent = componentBuilder.build()
        Log.d(TAG, "Сессия пользователя $userId создана")
    }

    /**
     * Вызывается при выходе пользователя.
     * Уничтожает UserSessionComponent, освобождая все сессионные зависимости.
     */
    fun logout() {
        userSessionComponent = null
        Log.d(TAG, "Сессия пользователя уничтожена")
    }

    /**
     * Возвращает текущий компонент сессии или бросает исключение,
     * если пользователь не авторизован.
     */
    fun getSessionComponent(): UserSessionComponent {
        return userSessionComponent
            ?: error("Пользователь не авторизован — UserSessionComponent недоступен")
    }

    companion object {
        private const val TAG = "UserSessionManager"
    }
}
