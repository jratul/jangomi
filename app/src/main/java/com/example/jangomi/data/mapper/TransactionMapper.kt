package com.example.jangomi.data.mapper

import com.example.jangomi.data.local.entity.TransactionEntity
import com.example.jangomi.domain.model.Category
import com.example.jangomi.domain.model.Transaction
import com.example.jangomi.domain.model.TransactionType
import java.time.LocalDate

fun TransactionEntity.toDomain() = Transaction(
    id = id,
    amount = amount,
    type = TransactionType.valueOf(type),
    category = Category.valueOf(category),
    merchantName = merchantName,
    memo = memo,
    date = LocalDate.parse(date)
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    amount = amount,
    type = type.name,
    category = category.name,
    merchantName = merchantName,
    memo = memo,
    date = date.toString()
)
