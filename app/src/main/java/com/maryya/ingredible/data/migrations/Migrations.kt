package com.maryya.ingredible.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE ItemList ADD COLUMN isActive INTEGER NOT NULL DEFAULT 1")
        }
    }

    // Future migrations can be added here
}