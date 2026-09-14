package com.fintrack.app

import com.fintrack.app.domain.model.Period
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.ZoneId

class PeriodTest {

    private val zone: ZoneId = ZoneId.of("Europe/Moscow")

    @Test
    fun `границы периода охватывают весь месяц`() {
        val april = Period(2026, 4)
        assertThat(april.startMillis(zone)).isEqualTo(TestData.millis(2026, 4, 1, 0, 0))
        // Последняя миллисекунда 30 апреля.
        assertThat(april.endMillis(zone)).isEqualTo(TestData.millis(2026, 5, 1, 0, 0) - 1)
    }

    @Test
    fun `операция в первую миллисекунду месяца попадает в период`() {
        val april = Period(2026, 4)
        val firstMoment = TestData.millis(2026, 4, 1, 0, 0)
        assertThat(firstMoment in april.startMillis(zone)..april.endMillis(zone)).isTrue()
    }

    @Test
    fun `операция за миллисекунду до месяца не попадает`() {
        val april = Period(2026, 4)
        val justBefore = april.startMillis(zone) - 1
        assertThat(justBefore in april.startMillis(zone)..april.endMillis(zone)).isFalse()
    }

    @Test
    fun `переход через границу года работает в обе стороны`() {
        assertThat(Period(2026, 12).next()).isEqualTo(Period(2027, 1))
        assertThat(Period(2026, 1).previous()).isEqualTo(Period(2025, 12))
    }

    @Test
    fun `период определяется по миллисекундам`() {
        assertThat(Period.of(TestData.millis(2026, 4, 8), zone)).isEqualTo(Period(2026, 4))
    }

    @Test
    fun `диапазон периодов идёт от новых к старым`() {
        val range = Period.range(Period(2026, 1), Period(2026, 4))
        assertThat(range).containsExactly(
            Period(2026, 4), Period(2026, 3), Period(2026, 2), Period(2026, 1),
        ).inOrder()
    }

    @Test
    fun `перевёрнутый диапазон пуст`() {
        assertThat(Period.range(Period(2026, 4), Period(2026, 1))).isEmpty()
    }
}
