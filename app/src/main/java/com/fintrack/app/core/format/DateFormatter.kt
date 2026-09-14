package com.fintrack.app.core.format

import com.fintrack.app.domain.model.Period
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Даты в русском формате макета: `08 апр., 09:30` в списке и `Апрель 2026`
 * в заголовке периода.
 *
 * Названия месяцев заданы явными массивами, а не через `Locale("ru")`:
 * сокращения в ICU менялись от версии к версии, а макет фиксирует конкретный
 * вид. Массивы покрыты unit-тестами.
 */
object DateFormatter {

    private val SHORT_MONTHS = arrayOf(
        "янв.", "февр.", "мар.", "апр.", "мая", "июн.",
        "июл.", "авг.", "сент.", "окт.", "нояб.", "дек.",
    )

    private val NOMINATIVE_MONTHS = arrayOf(
        "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь",
    )

    private val GENITIVE_MONTHS = arrayOf(
        "января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря",
    )

    private val SHORT_NOMINATIVE_MONTHS = arrayOf(
        "Янв.", "Февр.", "Март", "Апр.", "Май", "Июнь",
        "Июль", "Авг.", "Сент.", "Окт.", "Нояб.", "Дек.",
    )

    /** Строка операции: `08 апр., 09:30`. */
    fun listDateTime(millis: Long, zone: ZoneId = ZoneId.systemDefault()): String {
        val dt = millis.toLocalDateTime(zone)
        return "%02d %s, %02d:%02d".format(
            dt.dayOfMonth,
            SHORT_MONTHS[dt.monthValue - 1],
            dt.hour,
            dt.minute,
        )
    }

    /** Заголовок периода: `Апрель 2026`. */
    fun periodTitle(period: Period): String =
        "${NOMINATIVE_MONTHS[period.month - 1]} ${period.year}"

    /** Короткое имя месяца для списка выбора: `Апр.`. */
    fun monthShort(period: Period): String = SHORT_NOMINATIVE_MONTHS[period.month - 1]

    /** Разделитель в полном списке операций: `08 апреля 2026`. */
    fun fullDate(millis: Long, zone: ZoneId = ZoneId.systemDefault()): String {
        val dt = millis.toLocalDateTime(zone)
        return "%02d %s %d".format(dt.dayOfMonth, GENITIVE_MONTHS[dt.monthValue - 1], dt.year)
    }

    /** Значение поля формы: `08.04.2026`. */
    fun formField(millis: Long, zone: ZoneId = ZoneId.systemDefault()): String {
        val dt = millis.toLocalDateTime(zone)
        return "%02d.%02d.%d".format(dt.dayOfMonth, dt.monthValue, dt.year)
    }

    /** Текст для TalkBack: `8 апреля, 09:30`. */
    fun spoken(millis: Long, zone: ZoneId = ZoneId.systemDefault()): String {
        val dt = millis.toLocalDateTime(zone)
        return "%d %s, %02d:%02d".format(
            dt.dayOfMonth,
            GENITIVE_MONTHS[dt.monthValue - 1],
            dt.hour,
            dt.minute,
        )
    }

    /** Начало дня для группировки списка по датам. */
    fun startOfDay(millis: Long, zone: ZoneId = ZoneId.systemDefault()): Long =
        millis.toLocalDateTime(zone).toLocalDate().atStartOfDay(zone).toInstant().toEpochMilli()

    fun isToday(millis: Long, zone: ZoneId = ZoneId.systemDefault()): Boolean =
        millis.toLocalDateTime(zone).toLocalDate() == LocalDate.now(zone)

    fun isYesterday(millis: Long, zone: ZoneId = ZoneId.systemDefault()): Boolean =
        millis.toLocalDateTime(zone).toLocalDate() == LocalDate.now(zone).minusDays(1)

    private fun Long.toLocalDateTime(zone: ZoneId): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(this), zone)
}
