package com.fintrack.app

import com.fintrack.app.core.format.MoneyFormatter
import com.fintrack.app.domain.model.TransactionType
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Locale

class MoneyFormatterTest {

    private lateinit var defaultLocale: Locale

    @Before
    fun setUp() {
        defaultLocale = Locale.getDefault()
    }

    @After
    fun tearDown() {
        Locale.setDefault(defaultLocale)
    }

    @Test
    fun `форматирует сумму как в макете`() {
        assertThat(MoneyFormatter.format(180_620)).isEqualTo("$1,806.20")
    }

    @Test
    fun `доход всегда со знаком плюс`() {
        assertThat(MoneyFormatter.formatSigned(220_010, TransactionType.INCOME))
            .isEqualTo("+$2,200.10")
    }

    @Test
    fun `расход всегда с типографским минусом`() {
        val formatted = MoneyFormatter.formatSigned(23_070, TransactionType.EXPENSE)
        assertThat(formatted).isEqualTo("−$230.70")
        // U+2212, а не дефис U+002D.
        assertThat(formatted.first()).isEqualTo('−')
    }

    @Test
    fun `положительный баланс показывается без знака`() {
        assertThat(MoneyFormatter.formatBalance(196_940)).isEqualTo("$1,969.40")
    }

    @Test
    fun `отрицательный баланс показывается с минусом`() {
        assertThat(MoneyFormatter.formatBalance(-12_030)).isEqualTo("−$120.30")
    }

    @Test
    fun `баланс за период всегда со знаком кроме нуля`() {
        assertThat(MoneyFormatter.formatPeriodBalance(196_940)).isEqualTo("+$1,969.40")
        assertThat(MoneyFormatter.formatPeriodBalance(-12_030)).isEqualTo("−$120.30")
        assertThat(MoneyFormatter.formatPeriodBalance(0)).isEqualTo("$0.00")
    }

    @Test
    fun `формат не зависит от системной локали`() {
        Locale.setDefault(Locale("ru", "RU"))
        assertThat(MoneyFormatter.format(180_620)).isEqualTo("$1,806.20")
    }

    @Test
    fun `группировка разрядов для больших сумм`() {
        assertThat(MoneyFormatter.format(123_456_789)).isEqualTo("$1,234,567.89")
    }

    @Test
    fun `разбирает целое число`() {
        val result = MoneyFormatter.parseToCents("12")
        assertThat(result).isInstanceOf(MoneyFormatter.ParseResult.Success::class.java)
        assertThat((result as MoneyFormatter.ParseResult.Success).cents).isEqualTo(1_200)
    }

    @Test
    fun `разбирает сумму через точку и через запятую одинаково`() {
        val dot = MoneyFormatter.parseToCents("12.5") as MoneyFormatter.ParseResult.Success
        val comma = MoneyFormatter.parseToCents("12,5") as MoneyFormatter.ParseResult.Success
        assertThat(dot.cents).isEqualTo(1_250)
        assertThat(comma.cents).isEqualTo(1_250)
    }

    @Test
    fun `отклоняет пустую строку`() {
        assertThat(failureOf("")).isEqualTo(MoneyFormatter.AmountError.EMPTY)
    }

    @Test
    fun `отклоняет ноль`() {
        assertThat(failureOf("0")).isEqualTo(MoneyFormatter.AmountError.NOT_POSITIVE)
        assertThat(failureOf("0.00")).isEqualTo(MoneyFormatter.AmountError.NOT_POSITIVE)
    }

    @Test
    fun `отклоняет нечисловой ввод`() {
        assertThat(failureOf("abc")).isEqualTo(MoneyFormatter.AmountError.NOT_A_NUMBER)
        assertThat(failureOf("-5")).isEqualTo(MoneyFormatter.AmountError.NOT_A_NUMBER)
    }

    @Test
    fun `отклоняет больше двух знаков после разделителя`() {
        assertThat(failureOf("12.555")).isEqualTo(MoneyFormatter.AmountError.TOO_MANY_DECIMALS)
    }

    @Test
    fun `отклоняет слишком большое значение`() {
        assertThat(failureOf("1234567890")).isEqualTo(MoneyFormatter.AmountError.TOO_LARGE)
    }

    @Test
    fun `озвучивает сумму словами с правильными окончаниями`() {
        assertThat(MoneyFormatter.spoken(100)).isEqualTo("1 доллар")
        assertThat(MoneyFormatter.spoken(300)).isEqualTo("3 доллара")
        assertThat(MoneyFormatter.spoken(1_100)).isEqualTo("11 долларов")
        assertThat(MoneyFormatter.spoken(413)).isEqualTo("4 доллара 13 центов")
        assertThat(MoneyFormatter.spoken(-23_070)).isEqualTo("230 долларов 70 центов")
    }

    private fun failureOf(raw: String): MoneyFormatter.AmountError =
        (MoneyFormatter.parseToCents(raw) as MoneyFormatter.ParseResult.Failure).error
}
