package com.fintrack.app.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fintrack.app.core.format.DateFormatter
import com.fintrack.app.domain.model.Transaction
import com.fintrack.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Операции одного дня под общим заголовком-разделителем. */
data class TransactionDayGroup(
    val dayStartMillis: Long,
    val transactions: List<Transaction>,
)

data class TransactionsUiState(
    val isLoading: Boolean = true,
    val groups: List<TransactionDayGroup> = emptyList(),
) {
    val isEmpty: Boolean get() = !isLoading && groups.isEmpty()
}

class TransactionsViewModel(
    repository: TransactionRepository,
) : ViewModel() {

    val uiState: StateFlow<TransactionsUiState> = repository.observeAll()
        .map { transactions ->
            TransactionsUiState(
                isLoading = false,
                groups = transactions
                    .groupBy { DateFormatter.startOfDay(it.dateTimeMillis) }
                    .map { (day, items) -> TransactionDayGroup(day, items) }
                    .sortedByDescending { it.dayStartMillis },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = TransactionsUiState(),
        )
}
