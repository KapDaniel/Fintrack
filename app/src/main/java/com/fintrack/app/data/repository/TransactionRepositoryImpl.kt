package com.fintrack.app.data.repository

import com.fintrack.app.data.local.dao.CategoryDao
import com.fintrack.app.data.local.dao.TransactionDao
import com.fintrack.app.data.local.entity.TransactionEntity
import com.fintrack.app.data.mapper.toDomain
import com.fintrack.app.domain.model.Category
import com.fintrack.app.domain.model.Period
import com.fintrack.app.domain.model.Transaction
import com.fintrack.app.domain.model.TransactionType
import com.fintrack.app.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : TransactionRepository {

    override fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeAll().map { list -> list.map { it.toDomain() } }.flowOn(ioDispatcher)

    override fun observeRecent(limit: Int): Flow<List<Transaction>> =
        transactionDao.observeRecent(limit).map { list -> list.map { it.toDomain() } }.flowOn(ioDispatcher)

    override fun observeAll(): Flow<List<Transaction>> =
        transactionDao.observeAll().map { list -> list.map { it.toDomain() } }.flowOn(ioDispatcher)

    override fun observeByPeriod(period: Period): Flow<List<Transaction>> =
        transactionDao.observeByPeriod(period.startMillis(), period.endMillis())
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)

    override fun observeBalanceCents(): Flow<Long> =
        transactionDao.observeBalanceCents().flowOn(ioDispatcher)

    override fun observeAvailablePeriods(): Flow<List<Period>> =
        transactionDao.observeDateBounds()
            .map { bounds ->
                val min = bounds.minMillis
                val max = bounds.maxMillis
                if (min == null || max == null) {
                    listOf(Period.current())
                } else {
                    val range = Period.range(Period.of(min), Period.of(max))
                    val current = Period.current()
                    if (range.contains(current)) range else (range + current).sortedDescending()
                }
            }
            .flowOn(ioDispatcher)

    override suspend fun addTransaction(
        amountCents: Long,
        type: TransactionType,
        categoryId: Long,
        dateTimeMillis: Long,
        comment: String?,
    ): Long = withContext(ioDispatcher) {
        transactionDao.insert(
            TransactionEntity(
                amountCents = amountCents,
                type = type,
                categoryId = categoryId,
                dateTimeMillis = dateTimeMillis,
                comment = comment?.trim()?.takeIf { it.isNotEmpty() },
            )
        )
    }
}
