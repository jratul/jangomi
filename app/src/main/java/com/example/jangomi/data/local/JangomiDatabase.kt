package com.example.jangomi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jangomi.data.local.dao.TransactionDao
import com.example.jangomi.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class JangomiDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
