package com.fintrack.app.domain.model

import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

/**
 * Календарный месяц как период отчёта.
 *
 * Границы считаются в часовом поясе устройства: [startMillis] — 00:00:00.000
 * первого дня, [endMillis] — 23:59:59.999 последнего.
 */
data class Period(val year: Int, val month: Int) : Comparable<Period> {

    private val yearMonth: YearMonth get() = YearMonth.of(year, month)

    fun startMillis(zone: ZoneId = ZoneId.systemDefault()): Long =
        yearMonth.atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()

    fun endMillis(zone: ZoneId = ZoneId.systemDefault()): Long =
        yearMonth.plusMonths(1).atDay(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1

    fun previous(): Period = yearMonth.minusMonths(1).toPeriod()

    fun next(): Period = yearMonth.plusMonths(1).toPeriod()

    override fun compareTo(other: Period): Int =
        compareValuesBy(this, other, Period::year, Period::month)

    companion object {
        fun of(millis: Long, zone: ZoneId = ZoneId.systemDefault()): Period =
            YearMonth.from(Instant.ofEpochMilli(millis).atZone(zone)).toPeriod()

        fun current(zone: ZoneId = ZoneId.systemDefault()): Period =
            YearMonth.from(LocalDate.now(zone)).toPeriod()

        /** Непрерывный список месяцев от [from] до [to] включительно, новые сверху. */
        fun range(from: Period, to: Period): List<Period> {
            if (from > to) return emptyList()
            val result = mutableListOf<Period>()
            var cursor = from
            while (cursor <= to) {
                result += cursor
                cursor = cursor.next()
            }
            return result.asReversed()
        }
    }
}

private fun YearMonth.toPeriod(): Period = Period(year, monthValue)
