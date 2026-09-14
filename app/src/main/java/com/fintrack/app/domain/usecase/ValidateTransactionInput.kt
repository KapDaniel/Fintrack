package com.fintrack.app.domain.usecase

import com.fintrack.app.core.format.MoneyFormatter

/**
 * Валидация формы «Новая операция». Возвращает коды ошибок, а не строки, чтобы
 * правила проверялись обычными unit-тестами без ресурсов Android.
 */
object ValidateTransactionInput {

    const val MAX_COMMENT_LENGTH = 120

    data class Input(
        val amountRaw: String,
        val categoryId: Long?,
        val dateMillis: Long?,
        val comment: String,
    )

    data class Result(
        val amountError: MoneyFormatter.AmountError?,
        val categoryMissing: Boolean,
        val dateMissing: Boolean,
        val commentTooLong: Boolean,
        val amountCents: Long?,
    ) {
        val isValid: Boolean
            get() = amountError == null && !categoryMissing && !dateMissing && !commentTooLong
    }

    operator fun invoke(input: Input): Result {
        val parsed = MoneyFormatter.parseToCents(input.amountRaw)
        val amountError = (parsed as? MoneyFormatter.ParseResult.Failure)?.error
        val amountCents = (parsed as? MoneyFormatter.ParseResult.Success)?.cents

        return Result(
            amountError = amountError,
            categoryMissing = input.categoryId == null,
            dateMissing = input.dateMillis == null,
            commentTooLong = input.comment.length > MAX_COMMENT_LENGTH,
            amountCents = amountCents,
        )
    }
}
