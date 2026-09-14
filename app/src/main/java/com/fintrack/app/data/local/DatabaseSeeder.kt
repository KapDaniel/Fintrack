package com.fintrack.app.data.local

import androidx.sqlite.db.SupportSQLiteDatabase
import com.fintrack.app.domain.model.TransactionType
import java.time.LocalDate
import java.time.ZoneId

/**
 * Демо-данные первого запуска.
 *
 * Вызывается один раз — из [androidx.room.RoomDatabase.Callback.onCreate], то есть
 * при создании файла базы. Повторные запуски приложения сид не трогают; чтобы
 * получить его заново, нужно очистить данные приложения.
 *
 * Даты в макете были привязаны к апрелю 2026 года. Здесь они сдвигаются
 * относительно текущего дня, иначе «Итоги» за текущий месяц оказались бы пустыми.
 */
object DatabaseSeeder {

    private data class SeedCategory(
        val name: String,
        val iconKey: String,
        val defaultType: TransactionType?,
    )

    private data class SeedTransaction(
        val daysAgo: Long,
        val hour: Int,
        val minute: Int,
        val comment: String?,
        val categoryName: String,
        val type: TransactionType,
        val amountCents: Long,
    )

    private val CATEGORIES = listOf(
        SeedCategory("Транспорт", "bus", TransactionType.EXPENSE),
        SeedCategory("Продукты", "shopping_bag", TransactionType.EXPENSE),
        SeedCategory("Коммунальные услуги", "key", TransactionType.EXPENSE),
        SeedCategory("Подписки и сервисы", "briefcase", TransactionType.EXPENSE),
        SeedCategory("Перевод", "repeat", null),
        SeedCategory("Зарплата", "piggy_bank", TransactionType.INCOME),
        SeedCategory("Другое", "credit_card", null),
    )

    private val TRANSACTIONS = listOf(
        SeedTransaction(0, 9, 30, null, "Транспорт", TransactionType.EXPENSE, 413),
        SeedTransaction(1, 16, 0, "Перевод со стороннего счёта", "Перевод", TransactionType.INCOME, 200_000),
        SeedTransaction(1, 12, 20, "LinkedIn", "Подписки и сервисы", TransactionType.EXPENSE, 500),
        SeedTransaction(2, 14, 7, "Оплата коммунальных услуг", "Коммунальные услуги", TransactionType.EXPENSE, 12_030),
        SeedTransaction(2, 11, 23, "Apple Pay", "Подписки и сервисы", TransactionType.EXPENSE, 500),
        SeedTransaction(2, 10, 5, "Оплата продуктов", "Продукты", TransactionType.EXPENSE, 6_050),
        SeedTransaction(3, 18, 40, "Кофе с собой", "Продукты", TransactionType.EXPENSE, 3_577),
        SeedTransaction(4, 10, 0, "Кэшбэк по карте", "Другое", TransactionType.INCOME, 15_000),
        SeedTransaction(6, 9, 0, "Проценты по вкладу", "Другое", TransactionType.INCOME, 5_010),
    )

    fun seed(db: SupportSQLiteDatabase, zone: ZoneId = ZoneId.systemDefault()) {
        val categoryIds = mutableMapOf<String, Long>()

        CATEGORIES.forEachIndexed { index, category ->
            val id = (index + 1).toLong()
            categoryIds[category.name] = id
            db.execSQL(
                "INSERT INTO categories (id, name, icon_key, default_type) VALUES (?, ?, ?, ?)",
                arrayOf<Any?>(id, category.name, category.iconKey, category.defaultType?.name),
            )
        }

        val today = LocalDate.now(zone)
        TRANSACTIONS.forEach { seed ->
            val millis = today
                .minusDays(seed.daysAgo)
                .atTime(seed.hour, seed.minute)
                .atZone(zone)
                .toInstant()
                .toEpochMilli()

            db.execSQL(
                """
                INSERT INTO transactions (amount_cents, type, category_id, date_time_millis, comment)
                VALUES (?, ?, ?, ?, ?)
                """.trimIndent(),
                arrayOf<Any?>(
                    seed.amountCents,
                    seed.type.name,
                    categoryIds.getValue(seed.categoryName),
                    millis,
                    seed.comment,
                ),
            )
        }
    }
}
