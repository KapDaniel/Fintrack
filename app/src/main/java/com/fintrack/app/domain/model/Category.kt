package com.fintrack.app.domain.model

/**
 * Категория операции.
 *
 * @param iconKey ключ иконки; в UI превращается в drawable (см. CategoryIcons).
 * @param defaultType тип, который подставляется по умолчанию; null — подходит обоим.
 */
data class Category(
    val id: Long,
    val name: String,
    val iconKey: String,
    val defaultType: TransactionType?,
)
