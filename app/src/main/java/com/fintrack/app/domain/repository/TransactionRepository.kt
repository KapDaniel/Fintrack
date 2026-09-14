package com.fintrack.app.domain.repository

import com.fintrack.app.domain.model.Category
import com.fintrack.app.domain.model.Period
import com.fintrack.app.domain.model.Transaction
import com.fintrack.app.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    fun observeCategories(): Flow<List<Category>>

    fun observeRecent(limit: Int): Flow<List<Transaction>>

    fun observeAll(): Flow<List<Transaction>>

    fun observeByPeriod(period: Period): Flow<List<Transaction>>

    /** Баланс по всем операциям: сумма доходов минус сумма расходов. */
    fun observeBalanceCents(): Flow<Long>

    /** Месяцы, в которых есть хотя бы одна операция; новые сверху. */
    fun observeAvailablePeriods(): Flow<List<Period>>

    suspend fun addTransaction(
        amountCents: Long,
        type: TransactionType,
        categoryId: Long,
        dateTimeMillis: Long,
        comment: String?,
    ): Long
}
