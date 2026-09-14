package com.fintrack.app.domain.usecase

import com.fintrack.app.domain.model.CategoryTotal
import com.fintrack.app.domain.model.DistributionSlice
import com.fintrack.app.domain.model.PeriodSummary
import com.fintrack.app.domain.model.Transaction
import com.fintrack.app.domain.model.TransactionType
import kotlin.math.roundToInt

/**
 * Чистые расчёты по списку операций: суммы, баланс, группировка по категориям
 * и распределение расходов. Ничего не знает про Android — целиком покрывается
 * обычными unit-тестами.
 */
object CalculateSummary {

    /** Сколько категорий показывать в блоке «Частые категории». */
    const val TOP_CATEGORIES = 4

    /** Сколько именованных сегментов в диаграмме; остальное схлопывается в «Другое». */
    const val DISTRIBUTION_SLICES = 3

    fun incomeCents(transactions: List<Transaction>): Long =
        transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amountCents }

    fun expenseCents(transactions: List<Transaction>): Long =
        transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amountCents }

    fun balanceCents(transactions: List<Transaction>): Long =
        incomeCents(transactions) - expenseCents(transactions)

    /**
     * Суммы по категориям, отсортированные по убыванию. Процент считается
     * внутри своего типа: расходы — от всех расходов, доходы — от всех доходов.
     */
    fun byCategory(transactions: List<Transaction>): List<CategoryTotal> {
        val income = incomeCents(transactions)
        val expense = expenseCents(transactions)
        return transactions
            .groupBy { it.category.id to it.type }
            .map { (_, group) ->
                val first = group.first()
                val amount = group.sumOf { it.amountCents }
                val base = if (first.type == TransactionType.INCOME) income else expense
                CategoryTotal(
                    category = first.category,
                    type = first.type,
                    amountCents = amount,
                    percent = percentOf(amount, base),
                )
            }
            .sortedByDescending { it.amountCents }
    }

    /** Топ категорий для экрана «Итоги». */
    fun topCategories(transactions: List<Transaction>, limit: Int = TOP_CATEGORIES): List<CategoryTotal> =
        byCategory(transactions).take(limit)

    /**
     * Распределение расходов: [DISTRIBUTION_SLICES] крупнейших категорий плюс
     * агрегированное «Другое». Проценты нормализуются так, чтобы сумма была 100.
     */
    fun distribution(
        transactions: List<Transaction>,
        otherLabel: String,
        slices: Int = DISTRIBUTION_SLICES,
    ): List<DistributionSlice> {
        val expenses = byCategory(transactions).filter { it.type == TransactionType.EXPENSE }
        if (expenses.isEmpty()) return emptyList()

        val named = expenses.take(slices)
        val restCents = expenses.drop(slices).sumOf { it.amountCents }
        val result = named.map { DistributionSlice(it.category.name, it.percent, isOther = false) }
        val namedPercent = result.sumOf { it.percent }

        return if (restCents > 0) {
            result + DistributionSlice(otherLabel, (100 - namedPercent).coerceAtLeast(0), isOther = true)
        } else if (namedPercent in 1..99) {
            // Округление съело остаток — добавляем его последнему сегменту.
            result.dropLast(1) + result.last().copy(percent = result.last().percent + (100 - namedPercent))
        } else {
            result
        }
    }

    fun summarize(transactions: List<Transaction>, otherLabel: String): PeriodSummary {
        if (transactions.isEmpty()) return PeriodSummary.EMPTY
        val income = incomeCents(transactions)
        val expense = expenseCents(transactions)
        return PeriodSummary(
            incomeCents = income,
            expenseCents = expense,
            balanceCents = income - expense,
            topCategories = topCategories(transactions),
            distribution = distribution(transactions, otherLabel),
            transactionCount = transactions.size,
        )
    }

    /** Доля в целых процентах; при нулевой базе — 0, без деления на ноль. */
    fun percentOf(amountCents: Long, baseCents: Long): Int =
        if (baseCents <= 0L) 0 else (amountCents * 100.0 / baseCents).roundToInt()
}
