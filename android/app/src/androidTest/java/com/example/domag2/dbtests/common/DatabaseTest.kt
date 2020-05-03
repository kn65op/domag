package com.example.domag2.dbtests.common

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.domag2.database.database.AppDatabase
import com.example.domag2.dbtests.data.fillData
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
open class DatabaseTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    protected lateinit var db: AppDatabase

    @Before
    fun createDb() {
        db = com.example.domag2.dbtests.data.createDb(ApplicationProvider.getApplicationContext())
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
    fun dummyTest() {

    }
}
