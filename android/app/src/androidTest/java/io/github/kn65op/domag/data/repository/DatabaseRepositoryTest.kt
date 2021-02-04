package io.github.kn65op.domag.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.isEmpty
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.github.kn65op.domag.application.modules.SqlDatabaseModule
import io.github.kn65op.domag.dbtests.common.getFromLiveData
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

    @Before
    fun prepareTestEnvironment() {
        injectObjects()
    }

    private fun injectObjects() {
        hiltRule.inject()
    }

    @Test
    fun shouldReturnNoCategories() {
        assertThat(getFromLiveData(repository.getAllCategories()), isEmpty)
    }

    @Test
    fun shouldReturnNoDepots() {
        assertThat(getFromLiveData(repository.getAllDepots()), isEmpty)
    }
}