package io.github.kn65op.domag.data.database.database

import android.content.Context

interface DatabaseFactory {
    fun createDatabase(applicationContext: Context): AppDatabase

    companion object {
        const val LOG_TAG = "DatabaseFactory"
    }
}
