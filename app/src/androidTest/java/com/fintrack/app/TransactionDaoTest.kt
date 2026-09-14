package com.fintrack.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fintrack.app.data.local.FinTrackDatabase
import com.fintrack.app.data.local.dao.CategoryDao
import com.fintrack.app.data.local.dao.TransactionDao
import com.fintrack.app.data.local.entity.CategoryEntity
import com.fintrack.app.data.local.entity.TransactionEntity
import com.fintrack.app.domain.model.TransactionType
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneId

@RunWith(AndroidJUnit4::class)
class TransactionDaoTest {

    private lateinit var database: FinTrackDatabase
    private lateinit var transactionDao: TransactionDao
    private lateinit var categoryDao: CategoryDao

    private val zone: ZoneId = ZoneId.systemDefault()

    private fun millis(year: Int, month: Int, day: Int, hour: Int = 12): Long =
        LocalDateTime.of(year, month, day, hour, 0).atZone(zone).toInstant().toEpochMilli()

    @Before
    fun createDatabase() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // In-memory: сид демо-данных не запускается, тесты изолированы друг от друга.
        database = Room.inMemoryDatabaseBuilder(context, FinTrackDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        transactionDao = database.transactionDao()
        categoryDao = database.categoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    private suspend fun insertCategory(
        name: String = "Транспорт",
        iconKey: String = "bus",
        defaultType: TransactionType? = TransactionType.EXPENSE,
    ): Long = categoryDao.insert(CategoryEntity(name = name, iconKey = iconKey, defaultType = defaultType))

    @Test
    fun вставка_и_чтение_возвращают_операцию_с_категорией() = runBlocking {
        val categoryId = insertCategory()
        transactionDao.insert(
            TransactionEntity(
                amountCents = 413,
                type = TransactionType.EXPENSE,
                categoryId = categoryId,
                dateTimeMillis = millis(2026, 4, 8),
                comment = "Метро",
            )
        )

        val all = transactionDao.observeAll().first()
        assertThat(all).hasSize(1)
        assertThat(all.first().transaction.amountCents).isEqualTo(413)
        assertThat(all.first().transaction.comment).isEqualTo("Метро")
        assertThat(all.first().category.name).isEqualTo("Транспорт")
    }

    @Test
    fun тип_операции_сохраняется_строкой_и_читается_обратно() = runBlocking {
        val categoryId = insertCategory(name = "Зарплата", iconKey = "piggy_bank", defaultType = TransactionType.INCOME)
        transactionDao.insert(
            TransactionEntity(
                amountCents = 200_000,
                type = TransactionType.INCOME,
                categoryId = categoryId,
                dateTimeMillis = millis(2026, 4, 7),
                comment = null,
            )
        )

        val saved = transactionDao.observeAll().first().single()
        assertThat(saved.transaction.type).isEqualTo(TransactionType.INCOME)
        assertThat(saved.category.defaultType).isEqualTo(TransactionType.INCOME)
    }

    @Test
    fun список_отсортирован_по_дате_убывания() = runBlocking {
        val categoryId = insertCategory()
        listOf(millis(2026, 4, 1), millis(2026, 4, 8), millis(2026, 4, 5)).forEach { date ->
            transactionDao.insert(
                TransactionEntity(
                    amountCents = 100,
                    type = TransactionType.EXPENSE,
                    categoryId = categoryId,
                    dateTimeMillis = date,
                    comment = null,
                )
            )
        }

        val dates = transactionDao.observeAll().first().map { it.transaction.dateTimeMillis }
        assertThat(dates).isInOrder(compareByDescending<Long> { it })
    }

    @Test
    fun фильтр_по_периоду_учитывает_границы() = runBlocking {
        val categoryId = insertCategory()
        val aprilStart = millis(2026, 4, 1, hour = 0)
        val mayStart = millis(2026, 5, 1, hour = 0)

        listOf(aprilStart - 1, aprilStart, millis(2026, 4, 15), mayStart - 1, mayStart).forEach { date ->
            transactionDao.insert(
                TransactionEntity(
                    amountCents = 100,
                    type = TransactionType.EXPENSE,
                    categoryId = categoryId,
                    dateTimeMillis = date,
                    comment = null,
                )
            )
        }

        val inApril = transactionDao.observeByPeriod(aprilStart, mayStart - 1).first()
        assertThat(inApril).hasSize(3)
    }

    @Test
    fun баланс_равен_доходам_минус_расходам() = runBlocking {
        val categoryId = insertCategory()
        transactionDao.insert(
            TransactionEntity(
                amountCents = 200_000,
                type = TransactionType.INCOME,
                categoryId = categoryId,
                dateTimeMillis = millis(2026, 4, 7),
                comment = null,
            )
        )
        transactionDao.insert(
            TransactionEntity(
                amountCents = 23_070,
                type = TransactionType.EXPENSE,
                categoryId = categoryId,
                dateTimeMillis = millis(2026, 4, 8),
                comment = null,
            )
        )

        assertThat(transactionDao.observeBalanceCents().first()).isEqualTo(176_930)
    }

    @Test
    fun баланс_пустой_базы_равен_нулю() = runBlocking {
        assertThat(transactionDao.observeBalanceCents().first()).isEqualTo(0L)
    }

    @Test
    fun лимит_последних_операций_соблюдается() = runBlocking {
        val categoryId = insertCategory()
        repeat(10) { index ->
            transactionDao.insert(
                TransactionEntity(
                    amountCents = 100L * (index + 1),
                    type = TransactionType.EXPENSE,
                    categoryId = categoryId,
                    dateTimeMillis = millis(2026, 4, index + 1),
                    comment = null,
                )
            )
        }

        assertThat(transactionDao.observeRecent(6).first()).hasSize(6)
    }

    @Test
    fun границы_дат_совпадают_с_минимумом_и_максимумом() = runBlocking {
        val categoryId = insertCategory()
        val first = millis(2026, 1, 15)
        val last = millis(2026, 4, 8)
        listOf(first, millis(2026, 3, 2), last).forEach { date ->
            transactionDao.insert(
                TransactionEntity(
                    amountCents = 100,
                    type = TransactionType.EXPENSE,
                    categoryId = categoryId,
                    dateTimeMillis = date,
                    comment = null,
                )
            )
        }

        val bounds = transactionDao.observeDateBounds().first()
        assertThat(bounds.minMillis).isEqualTo(first)
        assertThat(bounds.maxMillis).isEqualTo(last)
    }

    @Test
    fun удаление_операции_убирает_её_из_списка() = runBlocking {
        val categoryId = insertCategory()
        val id = transactionDao.insert(
            TransactionEntity(
                amountCents = 500,
                type = TransactionType.EXPENSE,
                categoryId = categoryId,
                dateTimeMillis = millis(2026, 4, 8),
                comment = null,
            )
        )

        transactionDao.deleteById(id)
        assertThat(transactionDao.observeAll().first()).isEmpty()
        assertThat(transactionDao.count()).isEqualTo(0)
    }

    @Test
    fun категория_с_операциями_не_удаляется() = runBlocking {
        val categoryId = insertCategory()
        transactionDao.insert(
            TransactionEntity(
                amountCents = 500,
                type = TransactionType.EXPENSE,
                categoryId = categoryId,
                dateTimeMillis = millis(2026, 4, 8),
                comment = null,
            )
        )

        val failed = runCatching { categoryDao.deleteById(categoryId) }.isFailure
        assertThat(failed).isTrue()
        assertThat(categoryDao.count()).isEqualTo(1)
    }

    @Test
    fun категории_сортируются_по_имени() = runBlocking {
        insertCategory(name = "Транспорт", iconKey = "bus")
        insertCategory(name = "Другое", iconKey = "credit_card", defaultType = null)
        insertCategory(name = "Продукты", iconKey = "shopping_bag")

        val names = categoryDao.observeAll().first().map { it.name }
        assertThat(names).containsExactly("Другое", "Продукты", "Транспорт").inOrder()
    }
}
