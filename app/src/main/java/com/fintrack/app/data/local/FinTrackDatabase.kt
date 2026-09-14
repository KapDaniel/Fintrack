package com.fintrack.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fintrack.app.data.local.dao.CategoryDao
import com.fintrack.app.data.local.dao.TransactionDao
import com.fintrack.app.data.local.entity.CategoryEntity
import com.fintrack.app.data.local.entity.TransactionEntity

@Database(
    entities = [CategoryEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class FinTrackDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    abstract fun transactionDao(): TransactionDao

    companion object {
        const val NAME = "fintrack.db"

        fun build(context: Context): FinTrackDatabase =
            Room.databaseBuilder(context.applicationContext, FinTrackDatabase::class.java, NAME)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        DatabaseSeeder.seed(db)
                    }
                })
                .build()
    }
}
