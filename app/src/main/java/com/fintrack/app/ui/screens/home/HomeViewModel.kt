package com.fintrack.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fintrack.app.domain.model.Period
import com.fintrack.app.domain.model.Transaction
import com.fintrack.app.domain.repository.TransactionRepository
import com.fintrack.app.domain.usecase.CalculateSummary
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val isLoading: Boolean = true,
    val balanceCents: Long = 0L,
    val incomeCents: Long = 0L,
    val expenseCents: Long = 0L,
    val recent: List<Transaction> = emptyList(),
    val more: List<Transaction> = emptyList(),
) {
    val isEmpty: Boolean get() = !isLoading && recent.isEmpty()
    val hasMore: Boolean get() = more.isNotEmpty()
}

class HomeViewModel(
    repository: TransactionRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.observeBalanceCents(),
        repository.observeByPeriod(Period.current()),
        repository.observeRecent(RECENT_TOTAL),
    ) { balanceCents, currentMonth, recent ->
        HomeUiState(
            isLoading = false,
            balanceCents = balanceCents,
            incomeCents = CalculateSummary.incomeCents(currentMonth),
            expenseCents = CalculateSummary.expenseCents(currentMonth),
            recent = recent.take(RECENT_VISIBLE),
            more = recent.drop(RECENT_VISIBLE),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = HomeUiState(),
    )

    private companion object {
        /** Сколько строк видно сразу — как в макете. */
        const val RECENT_VISIBLE = 6

        /** Ещё три прячутся под «Показать ещё операции». */
        const val RECENT_TOTAL = 9

        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
