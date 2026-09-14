package com.fintrack.app.ui.screens.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fintrack.app.domain.model.Period
import com.fintrack.app.domain.model.PeriodSummary
import com.fintrack.app.domain.model.Transaction
import com.fintrack.app.domain.repository.TransactionRepository
import com.fintrack.app.domain.usecase.CalculateSummary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class SummaryUiState(
    val isLoading: Boolean = true,
    val period: Period = Period.current(),
    val availablePeriods: List<Period> = emptyList(),
    val summary: PeriodSummary = PeriodSummary.EMPTY,
    val recent: List<Transaction> = emptyList(),
) {
    val isEmpty: Boolean get() = !isLoading && summary.isEmpty
}

class SummaryViewModel(
    repository: TransactionRepository,
    private val otherCategoryLabel: String,
) : ViewModel() {

    private val selectedPeriod = MutableStateFlow(Period.current())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val periodTransactions = selectedPeriod.flatMapLatest { period ->
        repository.observeByPeriod(period)
    }

    val uiState: StateFlow<SummaryUiState> = combine(
        selectedPeriod,
        periodTransactions,
        repository.observeAvailablePeriods(),
    ) { period, transactions, availablePeriods ->
        SummaryUiState(
            isLoading = false,
            period = period,
            availablePeriods = availablePeriods,
            summary = CalculateSummary.summarize(transactions, otherCategoryLabel),
            recent = transactions.take(RECENT_LIMIT),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = SummaryUiState(),
    )

    fun onPeriodSelected(period: Period) {
        selectedPeriod.value = period
    }

    fun onPreviousPeriod() {
        selectedPeriod.value = selectedPeriod.value.previous()
    }

    fun onNextPeriod() {
        selectedPeriod.value = selectedPeriod.value.next()
    }

    private companion object {
        const val RECENT_LIMIT = 5
    }
}
