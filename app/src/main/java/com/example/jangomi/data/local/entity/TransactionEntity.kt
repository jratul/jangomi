package com.example.jangomi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val amount: Long,
    val type: String,
    val category: String,
    val merchantName: String,
    val memo: String,
    val date: String // ISO-8601: yyyy-MM-dd
)
