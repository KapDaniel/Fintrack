package com.fintrack.app.domain.model

/**
 * Операция.
 *
 * @param amountCents всегда положительное значение в центах; знак определяется [type].
 * @param dateTimeMillis момент операции в epoch-миллисекундах UTC.
 * @param comment необязательный комментарий, до 120 символов.
 */
data class Transaction(
    val id: Long,
    val amountCents: Long,
    val type: TransactionType,
    val category: Category,
    val dateTimeMillis: Long,
    val comment: String?,
) {
    /** Вклад операции в баланс: доход со знаком «+», расход со знаком «−». */
    val signedCents: Long
        get() = if (type == TransactionType.INCOME) amountCents else -amountCents

    /** Что показывать как название строки: комментарий, иначе имя категории. */
    val title: String
        get() = comment?.takeIf { it.isNotBlank() } ?: category.name
}
