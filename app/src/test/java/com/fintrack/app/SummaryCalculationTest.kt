package com.fintrack.app

import com.fintrack.app.domain.model.TransactionType
import com.fintrack.app.domain.usecase.CalculateSummary
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SummaryCalculationTest {

    private val transactions = TestData.mockupTransactions()

    @Test
    fun `сумма доходов совпадает с макетом`() {
        assertThat(CalculateSummary.incomeCents(transactions)).isEqualTo(220_010)
    }

    @Test
    fun `сумма расходов совпадает с макетом`() {
        assertThat(CalculateSummary.expenseCents(transactions)).isEqualTo(23_070)
    }

    @Test
    fun `баланс равен разнице доходов и расходов`() {
        assertThat(CalculateSummary.balanceCents(transactions)).isEqualTo(196_940)
    }

    @Test
    fun `баланс отрицательный когда расходов больше`() {
        val onlyExpenses = listOf(
            TestData.transaction(1, 5_000, TransactionType.EXPENSE, TestData.transport),
            TestData.transaction(2, 1_000, TransactionType.INCOME, TestData.transfer),
        )
        assertThat(CalculateSummary.balanceCents(onlyExpenses)).isEqualTo(-4_000)
    }

    @Test
    fun `пустой список даёт нули без деления на ноль`() {
        assertThat(CalculateSummary.incomeCents(emptyList())).isEqualTo(0)
        assertThat(CalculateSummary.expenseCents(emptyList())).isEqualTo(0)
        assertThat(CalculateSummary.balanceCents(emptyList())).isEqualTo(0)
        assertThat(CalculateSummary.byCategory(emptyList())).isEmpty()
        assertThat(CalculateSummary.distribution(emptyList(), "Другое")).isEmpty()
        assertThat(CalculateSummary.summarize(emptyList(), "Другое").isEmpty).isTrue()
    }

    @Test
    fun `группировка складывает суммы одной категории`() {
        val groceries = CalculateSummary.byCategory(transactions)
            .single { it.category.id == TestData.groceries.id }
        // 60.50 + 35.77
        assertThat(groceries.amountCents).isEqualTo(9_627)
    }

    @Test
    fun `группировка отсортирована по убыванию суммы`() {
        val amounts = CalculateSummary.byCategory(transactions).map { it.amountCents }
        assertThat(amounts).isInOrder(compareByDescending<Long> { it })
    }

    @Test
    fun `доход и расход по одной категории не смешиваются`() {
        val mixed = listOf(
            TestData.transaction(1, 1_000, TransactionType.EXPENSE, TestData.transfer),
            TestData.transaction(2, 3_000, TransactionType.INCOME, TestData.transfer),
        )
        val totals = CalculateSummary.byCategory(mixed)
        assertThat(totals).hasSize(2)
        assertThat(totals.map { it.type })
            .containsExactly(TransactionType.INCOME, TransactionType.EXPENSE)
    }

    @Test
    fun `процент расхода считается от всех расходов`() {
        val utilities = CalculateSummary.byCategory(transactions)
            .single { it.category.id == TestData.utilities.id }
        // 120.30 из 230.70 — 52%
        assertThat(utilities.percent).isEqualTo(52)
    }

    @Test
    fun `процент дохода считается от всех доходов`() {
        val transfer = CalculateSummary.byCategory(transactions)
            .single { it.category.id == TestData.transfer.id }
        // 2000.00 из 2200.10 — 91%
        assertThat(transfer.percent).isEqualTo(91)
    }

    @Test
    fun `процент при нулевой базе равен нулю`() {
        assertThat(CalculateSummary.percentOf(100, 0)).isEqualTo(0)
        assertThat(CalculateSummary.percentOf(0, 0)).isEqualTo(0)
    }

    @Test
    fun `топ категорий ограничен четырьмя`() {
        assertThat(CalculateSummary.topCategories(transactions)).hasSize(4)
    }

    @Test
    fun `распределение содержит три категории и Другое`() {
        val slices = CalculateSummary.distribution(transactions, "Другое")
        assertThat(slices).hasSize(4)
        assertThat(slices.last().isOther).isTrue()
        assertThat(slices.last().label).isEqualTo("Другое")
    }

    @Test
    fun `сумма долей распределения равна ста процентам`() {
        val slices = CalculateSummary.distribution(transactions, "Другое")
        assertThat(slices.sumOf { it.percent }).isEqualTo(100)
    }

    @Test
    fun `распределение не содержит доходов`() {
        val slices = CalculateSummary.distribution(transactions, "Другое")
        assertThat(slices.map { it.label }).doesNotContain(TestData.transfer.name)
    }

    @Test
    fun `сводка собирает все агрегаты разом`() {
        val summary = CalculateSummary.summarize(transactions, "Другое")
        assertThat(summary.incomeCents).isEqualTo(220_010)
        assertThat(summary.expenseCents).isEqualTo(23_070)
        assertThat(summary.balanceCents).isEqualTo(196_940)
        assertThat(summary.transactionCount).isEqualTo(9)
        assertThat(summary.isEmpty).isFalse()
    }
}
