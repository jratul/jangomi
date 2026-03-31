package com.example.jangomi.data.repository

import com.example.jangomi.data.local.dao.TransactionDao
import com.example.jangomi.data.mapper.toDomain
import com.example.jangomi.data.mapper.toEntity
import com.example.jangomi.domain.model.Transaction
import com.example.jangomi.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> =
        dao.getAllTransactions().map { list -> list.map { it.toDomain() } }

    override fun getTransactionsByMonth(year: Int, month: Int): Flow<List<Transaction>> {
        val yearMonth = "%04d-%02d".format(year, month)
        return dao.getTransactionsByMonth(yearMonth).map { list -> list.map { it.toDomain() } }
    }

    override fun searchTransactions(query: String): Flow<List<Transaction>> =
        dao.searchTransactions(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getTransactionById(id: Long): Transaction? =
        dao.getTransactionById(id)?.toDomain()

    override suspend fun insertTransaction(transaction: Transaction): Long =
        dao.insertTransaction(transaction.toEntity())

    override suspend fun updateTransaction(transaction: Transaction) =
        dao.updateTransaction(transaction.toEntity())

    override suspend fun deleteTransaction(id: Long) =
        dao.deleteTransaction(id)
}
