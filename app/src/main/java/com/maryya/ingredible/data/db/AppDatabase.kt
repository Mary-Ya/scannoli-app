package com.maryya.ingredible.data.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.maryya.ingredible.data.dao.ItemDao
import com.maryya.ingredible.data.dao.ItemListDao
import com.maryya.ingredible.data.entity.Item
import com.maryya.ingredible.data.entity.ItemList
import com.maryya.ingredible.data.migrations.Migrations

@Database(entities = [Item::class, ItemList::class], version = 2)
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
                )
                    .addMigrations(Migrations.MIGRATION_1_2) // Include migrations like this
                    .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
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
