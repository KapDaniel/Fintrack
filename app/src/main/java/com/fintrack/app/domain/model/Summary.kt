package com.fintrack.app.domain.model

/** Сумма по одной категории за период и её доля внутри своего типа. */
data class CategoryTotal(
    val category: Category,
    val type: TransactionType,
    val amountCents: Long,
    val percent: Int,
)

/** Сегмент диаграммы распределения расходов. */
data class DistributionSlice(
    val label: String,
    val percent: Int,
    val isOther: Boolean,
)

/** Агрегаты за период. */
data class PeriodSummary(
    val incomeCents: Long,
    val expenseCents: Long,
    val balanceCents: Long,
    val topCategories: List<CategoryTotal>,
    val distribution: List<DistributionSlice>,
    val transactionCount: Int,
) {
    val isEmpty: Boolean get() = transactionCount == 0

    companion object {
        val EMPTY = PeriodSummary(
            incomeCents = 0L,
            expenseCents = 0L,
            balanceCents = 0L,
            topCategories = emptyList(),
            distribution = emptyList(),
            transactionCount = 0,
        )
    }
}
