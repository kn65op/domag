package io.github.kn65op.domag.database.database.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Log.i(LOG_TAG, "Add category id field to consume")
        database.execSQL("ALTER TABLE Consume ADD COLUMN categoryId INTEGER NOT NULL DEFAULT 1")
    }
}
