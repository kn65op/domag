package io.github.kn65op.domag2.database.database

import android.content.Context
import io.github.kn65op.domag2.database.database.AppDatabase

interface DatabaseFactory {
    fun createDatabase(applicationContext: Context): AppDatabase

    companion object {
        val LOG_TAG = "DatabaseFactory"
    }
}
