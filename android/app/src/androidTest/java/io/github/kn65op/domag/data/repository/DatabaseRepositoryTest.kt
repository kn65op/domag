package io.github.kn65op.domag.data.repository

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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZonedDateTime
import javax.inject.Inject

open class DatabaseRepositoryBaseTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
}

private val oneElement = 1

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <FlowContent> validateFlowFirstElement(
    flow: Flow<FlowContent>,
    validationFunction: (FlowContent) -> Unit
) {
    flow.take(oneElement).collect { validationFunction(it) }
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
    fun shouldReturnNoCategories(): Unit = runBlocking {
        validateFlowFirstElement(repository.getAllCategories()) {
            assertThat(it, isEmpty)
        }
    }

    @Test
    fun shouldReturnNoDepots() = runBlocking {
        validateFlowFirstElement(repository.getAllDepots()) {
            assertThat(it, isEmpty)
        }
    }

    @Test
    fun shouldReturnNoItems() = runBlocking {
        validateFlowFirstElement(repository.getAllItems()) {
            assertThat(it, isEmpty)
        }
    }

    @Test
    fun shouldReturnNoCategory() = runBlocking {
        validateFlowFirstElement(repository.getCategory(notExistingEntry)) {
            assertThat(it, absent())
        }
    }

    @Test
    fun shouldReturnNoDepot() = runBlocking {
        validateFlowFirstElement(repository.getDepot(notExistingEntry)) {
            assertThat(it, absent())
        }
    }

    @Test
    fun shouldReturnNoItem() = runBlocking {
        validateFlowFirstElement(repository.getItem(notExistingEntry)) {
            assertThat(it, absent())
        }
    }

    @Test
    fun shouldGetItemsWithBestBefore() = runBlocking {
        validateFlowFirstElement(repository.getItemsWithBestBeforeBefore(ZonedDateTime.now())) {
            assertThat(it, isEmpty)
        }
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

    private val nonExistingCategoryId = 34
    private val existingId = 1

    @Before
    fun prepareTestEnvironment() {
        injectObjects()
        fillDatabase()
    }

    @After
    fun cleanup() {
        db.clearAllTables()
        db.close()
    }

    private fun injectObjects() {
        hiltRule.inject()
    }

    private fun fillDatabase() {
        fillData(db)
    }

    @Test
    fun categoriesShouldNotBeEmpty() = runBlocking {
        validateFlowFirstElement(repository.getAllCategories()) {
            assertThat(it, !isEmpty)
        }
    }

    @Test
    fun shouldFoundExistingCategory() = runBlocking {
        validateFlowFirstElement(repository.getCategory(existingId)) {
            assertThat(it, present())
        }
    }

    @Test
    fun itemsShouldNotBeEmpty() = runBlocking {
        validateFlowFirstElement(repository.getAllItems()) {
            assertThat(it, !isEmpty)
        }
    }

    @Test
    fun shouldFoundExistingItem() = runBlocking {
        validateFlowFirstElement(repository.getItem(existingId)) {
            assertThat(it, present())
        }
    }

    @Test
    fun depotsShouldNotBeEmpty() = runBlocking {
        validateFlowFirstElement(repository.getAllDepots()) {
            assertThat(it, !isEmpty)
        }
    }

    @Test
    fun shouldFoundExistingDepot() = runBlocking {
        validateFlowFirstElement(repository.getDepot(existingId)) {
            assertThat(it, present())
        }
    }

    @Test
    fun shouldGetItemsWithBestBefore() = runBlocking {
        validateFlowFirstElement(repository.getItemsWithBestBeforeBefore(ZonedDateTime.now())) {
            assertThat(it, !isEmpty)
        }
    }
}