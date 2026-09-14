package com.fintrack.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fintrack.app.domain.model.TransactionType

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["category_id"]),
        Index(value = ["date_time_millis"]),
    ],
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /** Всегда положительное значение в центах: никаких Float/Double для денег. */
    @ColumnInfo(name = "amount_cents")
    val amountCents: Long,

    @ColumnInfo(name = "type")
    val type: TransactionType,

    @ColumnInfo(name = "category_id")
    val categoryId: Long,

    /** Момент операции в epoch-миллисекундах UTC. */
    @ColumnInfo(name = "date_time_millis")
    val dateTimeMillis: Long,

    @ColumnInfo(name = "comment")
    val comment: String?,
)
