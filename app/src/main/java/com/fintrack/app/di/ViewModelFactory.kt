package com.fintrack.app.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.fintrack.app.FinTrackApplication
import com.fintrack.app.R
import com.fintrack.app.ui.screens.add.AddTransactionViewModel
import com.fintrack.app.ui.screens.home.HomeViewModel
import com.fintrack.app.ui.screens.summary.SummaryViewModel
import com.fintrack.app.ui.screens.transactions.TransactionsViewModel

/**
 * Одна фабрика на все ViewModel приложения — при ручном DI этого достаточно.
 */
val AppViewModelFactory: ViewModelProvider.Factory = viewModelFactory {
    initializer { HomeViewModel(app().container.transactionRepository) }
    initializer { TransactionsViewModel(app().container.transactionRepository) }
    initializer { AddTransactionViewModel(app().container.transactionRepository) }
    initializer {
        val application = app()
        SummaryViewModel(
            repository = application.container.transactionRepository,
            otherCategoryLabel = application.getString(R.string.summary_other),
        )
    }
}

private fun CreationExtras.app(): FinTrackApplication =
    this[APPLICATION_KEY] as FinTrackApplication
