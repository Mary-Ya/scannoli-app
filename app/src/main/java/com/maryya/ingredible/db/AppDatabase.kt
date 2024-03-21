package com.maryya.ingredible.db

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.maryya.ingredible.dao.ItemDao
import com.maryya.ingredible.dao.ItemListDao
import com.maryya.ingredible.entity.Item
import com.maryya.ingredible.entity.ItemList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [Item::class, ItemList::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun itemListDao(): ItemListDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            Log.d("AppDatabase", "Requesting database instance")
            return INSTANCE ?: synchronized(this) {
                Log.d("AppDatabase", "Creating new database instance")
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "item_database"
                ) .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                    .fallbackToDestructiveMigration()
                    .enableMultiInstanceInvalidation()
                    .build()
                INSTANCE = instance
                Log.d("AppDatabase", "Database instance created")
                instance
            }
        }
    }
}
