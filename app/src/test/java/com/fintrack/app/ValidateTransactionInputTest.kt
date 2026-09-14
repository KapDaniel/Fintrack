package com.fintrack.app

import com.fintrack.app.core.format.MoneyFormatter
import com.fintrack.app.domain.usecase.ValidateTransactionInput
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ValidateTransactionInputTest {

    private fun validate(
        amount: String = "12.50",
        categoryId: Long? = 1L,
        dateMillis: Long? = TestData.millis(2026, 4, 8),
        comment: String = "",
    ) = ValidateTransactionInput(
        ValidateTransactionInput.Input(amount, categoryId, dateMillis, comment)
    )

    @Test
    fun `корректная форма проходит валидацию`() {
        val result = validate()
        assertThat(result.isValid).isTrue()
        assertThat(result.amountCents).isEqualTo(1_250)
    }

    @Test
    fun `пустая сумма не проходит`() {
        val result = validate(amount = "")
        assertThat(result.isValid).isFalse()
        assertThat(result.amountError).isEqualTo(MoneyFormatter.AmountError.EMPTY)
    }

    @Test
    fun `нулевая сумма не проходит`() {
        assertThat(validate(amount = "0").amountError)
            .isEqualTo(MoneyFormatter.AmountError.NOT_POSITIVE)
    }

    @Test
    fun `без категории форма невалидна`() {
        val result = validate(categoryId = null)
        assertThat(result.isValid).isFalse()
        assertThat(result.categoryMissing).isTrue()
    }

    @Test
    fun `без даты форма невалидна`() {
        val result = validate(dateMillis = null)
        assertThat(result.isValid).isFalse()
        assertThat(result.dateMissing).isTrue()
    }

    @Test
    fun `комментарий ровно на границе допустим`() {
        val comment = "a".repeat(ValidateTransactionInput.MAX_COMMENT_LENGTH)
        val result = validate(comment = comment)
        assertThat(result.commentTooLong).isFalse()
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun `комментарий длиннее ста двадцати символов не проходит`() {
        val comment = "a".repeat(ValidateTransactionInput.MAX_COMMENT_LENGTH + 1)
        val result = validate(comment = comment)
        assertThat(result.commentTooLong).isTrue()
        assertThat(result.isValid).isFalse()
    }

    @Test
    fun `несколько ошибок сообщаются одновременно`() {
        val result = validate(amount = "abc", categoryId = null)
        assertThat(result.amountError).isEqualTo(MoneyFormatter.AmountError.NOT_A_NUMBER)
        assertThat(result.categoryMissing).isTrue()
        assertThat(result.amountCents).isNull()
    }
}
