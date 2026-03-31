package com.example.jangomi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.jangomi.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query(
        "SELECT * FROM transactions WHERE date LIKE :yearMonth || '%' ORDER BY date DESC, id DESC"
    )
    fun getTransactionsByMonth(yearMonth: String): Flow<List<TransactionEntity>>

    @Query(
        """SELECT * FROM transactions
           WHERE merchantName LIKE '%' || :query || '%'
              OR memo LIKE '%' || :query || '%'
              OR category LIKE '%' || :query || '%'
           ORDER BY date DESC, id DESC"""
    )
    fun searchTransactions(query: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(entity: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(entity: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)
}
