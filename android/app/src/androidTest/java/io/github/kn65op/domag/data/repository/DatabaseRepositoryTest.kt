package io.github.kn65op.domag.data.repository

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.natpryce.hamkrest.*
import com.natpryce.hamkrest.assertion.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.github.kn65op.domag.application.modules.SqlDatabaseModule
import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.dbtests.data.fillData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

open class DatabaseRepositoryBaseTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}

@RunWith(AndroidJUnit4::class)
@UninstallModules(SqlDatabaseModule::class)
@HiltAndroidTest
class DatabaseRepositoryTestWhenDatabaseEmpty : DatabaseRepositoryBaseTest() {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Inject
    lateinit var repository: DatabaseRepository

    private val notExistingEntry = 34

    @Before
    fun prepareTestEnvironment() {
        injectObjects()
    }

    private fun injectObjects() {
        hiltRule.inject()
    }

    @Test
    fun shouldReturnNoCategories() = runBlocking {
        assertThat(repository.getAllCategories().first(), isEmpty)
    }

    @Test
    fun shouldReturnNoDepots() = runBlocking {
        assertThat((repository.getAllDepots().first()), isEmpty)
    }

    @Test
    fun shouldReturnNoItems() = runBlocking {
        assertThat((repository.getAllItems().first()), isEmpty)
    }

    @Test
    fun shouldReturnNoCategory() = runBlocking {
        assertThat(repository.getCategory(notExistingEntry).first(), equalTo(null))
    }

    @Test
    fun shouldReturnNoDepot() = runBlocking {
        assertThat(repository.getDepot(notExistingEntry).first(), equalTo(null))
    }

    @Test
    fun shouldReturnNoItem() = runBlocking {
        assertThat(repository.getItem(notExistingEntry).first(), equalTo(null))
    }
}

@RunWith(AndroidJUnit4::class)
@UninstallModules(SqlDatabaseModule::class)
@HiltAndroidTest
class DatabaseRepositoryTestWhenDatabaseFilled : DatabaseRepositoryBaseTest() {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Inject
    lateinit var repository: DatabaseRepository

    @Inject
    lateinit var db: AppDatabase

    private val notExistingEntryId = 34
    private val existingEntryId = 34

    @Before
    fun prepareTestEnvironment() {
        injectObjects()
    }

    @After
    fun cleanup() {
        db.clearAllTables()
        db.close()
    }

    private fun injectObjects() {
        hiltRule.inject()
    }

    fun fillDatabase() {
        fillData(db)
    }

    @Test
    fun shouldNotBeEmpty() = runBlocking {
        assertThat(repository.getAllCategories().first().size, greaterThan(0))
    }

    @Test
    fun shouldNotFoundNotExistingEntry() = runBlocking {
        assertThat(repository.getCategory(notExistingEntryId).first(), absent())
    }

    @Test
    fun shouldFoundExistingEntry() = runBlocking {
        assertThat(repository.getCategory(notExistingEntryId).first(), present())
    }
}