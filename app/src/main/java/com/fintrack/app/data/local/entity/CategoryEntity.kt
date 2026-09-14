package com.fintrack.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fintrack.app.domain.model.TransactionType

@Entity(
    tableName = "categories",
    indices = [Index(value = ["name"], unique = true)],
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    /** Ключ иконки; в UI превращается в drawable. */
    @ColumnInfo(name = "icon_key")
    val iconKey: String,

    /** Тип по умолчанию для этой категории; null — подходит и доходу, и расходу. */
    @ColumnInfo(name = "default_type")
    val defaultType: TransactionType?,
)
