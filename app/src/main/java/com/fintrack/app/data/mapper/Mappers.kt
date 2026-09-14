package com.fintrack.app.data.mapper

import com.fintrack.app.data.local.entity.CategoryEntity
import com.fintrack.app.data.local.entity.TransactionWithCategory
import com.fintrack.app.domain.model.Category
import com.fintrack.app.domain.model.Transaction

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    iconKey = iconKey,
    defaultType = defaultType,
)

fun TransactionWithCategory.toDomain(): Transaction = Transaction(
    id = transaction.id,
    amountCents = transaction.amountCents,
    type = transaction.type,
    category = category.toDomain(),
    dateTimeMillis = transaction.dateTimeMillis,
    comment = transaction.comment,
)
