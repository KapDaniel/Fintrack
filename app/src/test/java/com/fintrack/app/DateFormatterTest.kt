package com.fintrack.app

import com.fintrack.app.core.format.DateFormatter
import com.fintrack.app.domain.model.Period
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class DateFormatterTest {

    private val zone: ZoneId = ZoneId.of("Europe/Moscow")

    private fun millis(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long =
        LocalDateTime.of(year, month, day, hour, minute).atZone(zone).toInstant().toEpochMilli()

    @Test
    fun `дата в списке совпадает с макетом`() {
        assertThat(DateFormatter.listDateTime(millis(2026, 4, 8, 9, 30), zone))
            .isEqualTo("08 апр., 09:30")
    }

    @Test
    fun `день и час выводятся с ведущим нулём`() {
        assertThat(DateFormatter.listDateTime(millis(2026, 1, 2, 3, 4), zone))
            .isEqualTo("02 янв., 03:04")
    }

    @Test
    fun `время в 24-часовом формате`() {
        assertThat(DateFormatter.listDateTime(millis(2026, 4, 7, 16, 0), zone))
            .isEqualTo("07 апр., 16:00")
    }

    @Test
    fun `все двенадцать сокращений месяцев`() {
        val expected = listOf(
            "янв.", "февр.", "мар.", "апр.", "мая", "июн.",
            "июл.", "авг.", "сент.", "окт.", "нояб.", "дек.",
        )
        val actual = (1..12).map {
            DateFormatter.listDateTime(millis(2026, it, 1, 0, 0), zone).substringAfter("01 ").substringBefore(",")
        }
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `заголовок периода в именительном падеже`() {
        assertThat(DateFormatter.periodTitle(Period(2026, 4))).isEqualTo("Апрель 2026")
        assertThat(DateFormatter.periodTitle(Period(2026, 12))).isEqualTo("Декабрь 2026")
    }

    @Test
    fun `полная дата в родительном падеже`() {
        assertThat(DateFormatter.fullDate(millis(2026, 4, 8, 9, 30), zone))
            .isEqualTo("08 апреля 2026")
    }

    @Test
    fun `дата поля формы в формате ДД ММ ГГГГ`() {
        assertThat(DateFormatter.formField(millis(2026, 4, 8, 9, 30), zone))
            .isEqualTo("08.04.2026")
    }

    @Test
    fun `озвучивание даты без ведущего нуля в дне`() {
        assertThat(DateFormatter.spoken(millis(2026, 4, 8, 9, 30), zone))
            .isEqualTo("8 апреля, 09:30")
    }

    @Test
    fun `начало дня отбрасывает время`() {
        val start = DateFormatter.startOfDay(millis(2026, 4, 8, 23, 59), zone)
        assertThat(start).isEqualTo(millis(2026, 4, 8, 0, 0))
    }
}
