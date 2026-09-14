package com.fintrack.app

import com.fintrack.app.domain.model.Category
import com.fintrack.app.domain.model.Transaction
import com.fintrack.app.domain.model.TransactionType
import java.time.LocalDateTime
import java.time.ZoneId

/** Общие данные для unit-тестов: те же категории и суммы, что в макете. */
object TestData {

    val zone: ZoneId = ZoneId.of("Europe/Moscow")

    val transport = Category(1, "Транспорт", "bus", TransactionType.EXPENSE)
    val groceries = Category(2, "Продукты", "shopping_bag", TransactionType.EXPENSE)
    val utilities = Category(3, "Коммунальные услуги", "key", TransactionType.EXPENSE)
    val subscriptions = Category(4, "Подписки и сервисы", "briefcase", TransactionType.EXPENSE)
    val transfer = Category(5, "Перевод", "repeat", null)
    val other = Category(7, "Другое", "credit_card", null)

    fun millis(year: Int, month: Int, day: Int, hour: Int = 12, minute: Int = 0): Long =
        LocalDateTime.of(year, month, day, hour, minute).atZone(zone).toInstant().toEpochMilli()

    fun transaction(
        id: Long,
        amountCents: Long,
        type: TransactionType,
        category: Category,
        dateTimeMillis: Long = millis(2026, 4, 8),
        comment: String? = null,
    ) = Transaction(
        id = id,
        amountCents = amountCents,
        type = type,
        category = category,
        dateTimeMillis = dateTimeMillis,
        comment = comment,
    )

    /**
     * Набор из макета: доходы +$2,200.10, расходы −$230.70, баланс +$1,969.40.
     */
    fun mockupTransactions(): List<Transaction> = listOf(
        transaction(1, 413, TransactionType.EXPENSE, transport),
        transaction(2, 200_000, TransactionType.INCOME, transfer, comment = "Перевод со стороннего счёта"),
        transaction(3, 500, TransactionType.EXPENSE, subscriptions, comment = "LinkedIn"),
        transaction(4, 12_030, TransactionType.EXPENSE, utilities, comment = "Оплата коммунальных услуг"),
        transaction(5, 500, TransactionType.EXPENSE, subscriptions, comment = "Apple Pay"),
        transaction(6, 6_050, TransactionType.EXPENSE, groceries, comment = "Оплата продуктов"),
        transaction(7, 3_577, TransactionType.EXPENSE, groceries, comment = "Кофе с собой"),
        transaction(8, 15_000, TransactionType.INCOME, other, comment = "Кэшбэк по карте"),
        transaction(9, 5_010, TransactionType.INCOME, other, comment = "Проценты по вкладу"),
    )
}
