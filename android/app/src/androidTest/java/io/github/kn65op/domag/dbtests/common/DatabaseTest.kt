package io.github.kn65op.domag.dbtests.common

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.github.kn65op.domag.application.modules.SqlDatabaseModule
import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.dbtests.data.fillData
import org.junit.After
import org.junit.Before
import org.junit.Rule

@UninstallModules(SqlDatabaseModule::class)
@HiltAndroidTest
open class DatabaseTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    protected lateinit var db: AppDatabase

    fun prepareTestEnvironment()
    {

    }

    @Before
    fun createDb() {
        db = io.github.kn65op.domag.dbtests.data.createDb(ApplicationProvider.getApplicationContext())
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
}
