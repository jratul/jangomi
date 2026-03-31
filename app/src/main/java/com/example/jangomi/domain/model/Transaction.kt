package com.example.jangomi.domain.model

import java.time.LocalDate

data class Transaction(
    val id: Long = 0L,
    val amount: Long,
    val type: TransactionType,
    val category: Category,
    val merchantName: String,
    val memo: String = "",
    val date: LocalDate
)
