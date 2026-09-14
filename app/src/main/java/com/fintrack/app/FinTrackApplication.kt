package com.fintrack.app

import android.app.Application
import com.fintrack.app.di.AppContainer

/**
 * Точка входа приложения. Держит контейнер зависимостей (база, репозиторий).
 */
class FinTrackApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
