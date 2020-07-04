package io.kn65op.domag2.database.database

import android.content.Context

interface DatabaseFactory {
    fun createDatabase(applicationContext: Context): AppDatabase

    companion object {
        val LOG_TAG = "DatabaseFactory"
    }
}
