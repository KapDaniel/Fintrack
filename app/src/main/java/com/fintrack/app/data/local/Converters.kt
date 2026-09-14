package com.fintrack.app.data.local

import androidx.room.TypeConverter
import com.fintrack.app.domain.model.TransactionType

/**
 * Тип операции хранится строкой: так дамп базы читаем глазами и порядок
 * констант в enum можно менять без миграции.
 */
class Converters {

    @TypeConverter
    fun fromTransactionType(type: TransactionType?): String? = type?.name

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? =
        value?.let { runCatching { TransactionType.valueOf(it) }.getOrNull() }
}
