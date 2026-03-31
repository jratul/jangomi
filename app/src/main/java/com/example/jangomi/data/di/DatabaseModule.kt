package com.example.jangomi.data.di

import android.content.Context
import androidx.room.Room
import com.example.jangomi.data.local.JangomiDatabase
import com.example.jangomi.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): JangomiDatabase =
        Room.databaseBuilder(context, JangomiDatabase::class.java, "jangomi.db").build()

    @Provides
    fun provideTransactionDao(db: JangomiDatabase): TransactionDao = db.transactionDao()
}
