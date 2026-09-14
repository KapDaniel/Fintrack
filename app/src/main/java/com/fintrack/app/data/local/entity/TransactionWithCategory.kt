package com.fintrack.app.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/** Операция вместе со своей категорией — читается одним запросом. */
data class TransactionWithCategory(
    @Embedded
    val transaction: TransactionEntity,

    @Relation(parentColumn = "category_id", entityColumn = "id")
    val category: CategoryEntity,
)
