package com.fintrack.app.core.format

import com.fintrack.app.domain.model.TransactionType
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

/**
 * Единственный источник правил отображения сумм (как в макете):
 * `$1,806.20`, доход всегда `+`, расход всегда `−` (U+2212 MINUS SIGN).
 *
 * Формат жёстко привязан к [Locale.US]: интерфейс русский, но валюта и
 * группировка разрядов остаются долларовыми, иначе получилось бы `1 806,20`.
 */
object MoneyFormatter {

    /** Типографский минус, а не дефис — ровно как в макете. */
    const val MINUS = '−'
    const val PLUS = '+'
    private const val CURRENCY = "$"

    private const val MAX_INTEGER_DIGITS = 9
    private const val CENTS_IN_UNIT = 100

    private fun newFormatter(): DecimalFormat =
        DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))

    /** Абсолютная величина без знака: `$1,806.20`. */
    fun format(cents: Long): String =
        CURRENCY + newFormatter().format(BigDecimal.valueOf(abs(cents), 2))

    /** Сумма операции со знаком её типа: `+$2,200.10` / `−$230.70`. */
    fun formatSigned(cents: Long, type: TransactionType): String = when (type) {
        TransactionType.INCOME -> "$PLUS${format(cents)}"
        TransactionType.EXPENSE -> "$MINUS${format(cents)}"
    }

    /** Баланс: положительный — без знака, отрицательный — с минусом. */
    fun formatBalance(cents: Long): String =
        if (cents < 0) "$MINUS${format(cents)}" else format(cents)

    /** Баланс за период: знак показывается всегда, кроме нуля. */
    fun formatPeriodBalance(cents: Long): String = when {
        cents > 0 -> "$PLUS${format(cents)}"
        cents < 0 -> "$MINUS${format(cents)}"
        else -> format(0)
    }

    /**
     * Разбор пользовательского ввода в центы.
     * Принимает `12`, `12.5`, `12,50`; отклоняет пустое, нечисловое,
     * больше двух знаков после разделителя и слишком большие значения.
     */
    fun parseToCents(raw: String): ParseResult {
        val normalized = raw.trim().replace(',', '.').replace(" ", "").replace(" ", "")
        if (normalized.isEmpty()) return ParseResult.Failure(AmountError.EMPTY)
        if (!normalized.matches(Regex("""\d*\.?\d*"""))) return ParseResult.Failure(AmountError.NOT_A_NUMBER)

        val parts = normalized.split('.')
        val integerPart = parts[0].ifEmpty { "0" }
        val fractionPart = parts.getOrNull(1).orEmpty()

        if (fractionPart.length > 2) return ParseResult.Failure(AmountError.TOO_MANY_DECIMALS)
        if (integerPart.length > MAX_INTEGER_DIGITS) return ParseResult.Failure(AmountError.TOO_LARGE)

        val units = integerPart.toLongOrNull() ?: return ParseResult.Failure(AmountError.NOT_A_NUMBER)
        val cents = fractionPart.padEnd(2, '0').toLongOrNull() ?: return ParseResult.Failure(AmountError.NOT_A_NUMBER)
        val total = units * CENTS_IN_UNIT + cents

        return if (total <= 0L) ParseResult.Failure(AmountError.NOT_POSITIVE) else ParseResult.Success(total)
    }

    /**
     * Текст для TalkBack: типографский минус читается непредсказуемо,
     * поэтому сумма проговаривается словами.
     */
    fun spoken(cents: Long): String {
        val absCents = abs(cents)
        val units = absCents / CENTS_IN_UNIT
        val rest = absCents % CENTS_IN_UNIT
        val dollars = "$units ${plural(units, "доллар", "доллара", "долларов")}"
        return if (rest == 0L) dollars else "$dollars $rest ${plural(rest, "цент", "цента", "центов")}"
    }

    private fun plural(value: Long, one: String, few: String, many: String): String {
        val mod100 = value % 100
        val mod10 = value % 10
        return when {
            mod100 in 11..14 -> many
            mod10 == 1L -> one
            mod10 in 2..4 -> few
            else -> many
        }
    }

    enum class AmountError { EMPTY, NOT_A_NUMBER, NOT_POSITIVE, TOO_MANY_DECIMALS, TOO_LARGE }

    sealed interface ParseResult {
        data class Success(val cents: Long) : ParseResult
        data class Failure(val error: AmountError) : ParseResult
    }
}
