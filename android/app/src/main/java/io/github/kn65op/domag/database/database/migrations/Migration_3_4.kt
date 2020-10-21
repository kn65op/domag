package io.github.kn65op.domag.database.database.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Log.i(LOG_TAG, "Create CategoryLimit table")
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `CategoryLimit` (`uid` INTEGER, `categoryId` INTEGER NOT NULL, `minimumDesiredAmount` INTEGER NOT NULL, PRIMARY KEY(`uid`))"
        )
    }
}
