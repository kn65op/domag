package com.example.domag2.dbtests

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.domag2.database.daos.ItemDao
import com.example.domag2.database.database.AppDatabase
import com.example.domag2.dbtests.data.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
open class ItemDatabaseTest {
    private lateinit var itemDao: ItemDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        db = createDb(ApplicationProvider.getApplicationContext())
        itemDao = db.itemDao()
    }

    @Before
    fun fillDatabase() {
        fillData(db)
    }

    @After
    fun closeDb() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun getAllItems() {
    }
}
