package com.fintrack.app.di

import android.content.Context
import com.fintrack.app.data.local.FinTrackDatabase
import com.fintrack.app.data.repository.TransactionRepositoryImpl
import com.fintrack.app.domain.repository.TransactionRepository

/**
 * Ручной контейнер зависимостей.
 *
 * Для трёх экранов Hilt избыточен: лишний плагин, лишний этап генерации и
 * заметный вес. Здесь всё создаётся лениво и живёт столько же, сколько процесс.
 */
class AppContainer(context: Context) {

    private val database: FinTrackDatabase by lazy { FinTrackDatabase.build(context) }

    val transactionRepository: TransactionRepository by lazy {
        TransactionRepositoryImpl(
            transactionDao = database.transactionDao(),
            categoryDao = database.categoryDao(),
        )
    }
}
