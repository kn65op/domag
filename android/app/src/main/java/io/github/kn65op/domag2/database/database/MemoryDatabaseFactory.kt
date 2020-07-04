package io.github.kn65op.domag2.database.database

import android.content.Context
import android.util.Log
import androidx.room.Room

class MemoryDatabaseFactory :
    DatabaseFactory {
    override fun createDatabase(applicationContext: Context): AppDatabase {
        Log.i(DatabaseFactory.LOG_TAG, "Memory")
        return getInstance(applicationContext)
    }

    fun clearDb() {
        setToNull()
    }

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(applicationContext: Context): AppDatabase = (instance ?: synchronized(this)
        {
            Log.i(DatabaseFactory.LOG_TAG, "get memory instance")
            instance ?: buildDatabase(applicationContext).also { instance = it }
        })

        fun setToNull() {
            instance?.clearAllTables()
            //instance?.close()
            //instance = null
        }

        private fun buildDatabase(applicationContext: Context) =
            Room.inMemoryDatabaseBuilder(
                applicationContext,
                AppDatabase::class.java
            ).build()
    }
}
