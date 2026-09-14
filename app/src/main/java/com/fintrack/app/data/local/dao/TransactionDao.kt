package com.fintrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.fintrack.app.data.local.entity.TransactionEntity
import com.fintrack.app.data.local.entity.TransactionWithCategory
import kotlinx.coroutines.flow.Flow

/** Границы дат по всей базе — из них строится список доступных периодов. */
data class DateBounds(
    val minMillis: Long?,
    val maxMillis: Long?,
)

@Dao
interface TransactionDao {

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date_time_millis DESC, id DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<TransactionWithCategory>>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date_time_millis DESC, id DESC")
    fun observeAll(): Flow<List<TransactionWithCategory>>

    @Transaction
    @Query(
        """
        SELECT * FROM transactions
        WHERE date_time_millis BETWEEN :fromMillis AND :toMillis
        ORDER BY date_time_millis DESC, id DESC
        """
    )
    fun observeByPeriod(fromMillis: Long, toMillis: Long): Flow<List<TransactionWithCategory>>

    @Query(
        """
        SELECT COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount_cents ELSE -amount_cents END), 0)
        FROM transactions
        """
    )
    fun observeBalanceCents(): Flow<Long>

    @Query("SELECT MIN(date_time_millis) AS minMillis, MAX(date_time_millis) AS maxMillis FROM transactions")
    fun observeDateBounds(): Flow<DateBounds>

    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionWithCategory?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transaction: TransactionEntity): Long

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun count(): Int
}
