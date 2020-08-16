package io.github.kn65op.domag.uitests

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import io.github.kn65op.domag.dbtests.data.fillData
import io.github.kn65op.domag.uitests.common.activityFactory
import io.github.kn65op.domag.uitests.common.factory
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
open class MainWindowTestSuite {

    @get:Rule
    var activityRule = ActivityTestRule(activityFactory, true, true)

    @Before
    fun fillDb() {
        val db = factory.createDatabase(ApplicationProvider.getApplicationContext())
        fillData(db)
        Thread.sleep(500) // WA for aynschronous DB calls
    }

    @After
    fun clearDb() {
        val db = factory.createDatabase(ApplicationProvider.getApplicationContext())
        db.clearAllTables()
        Thread.sleep(500) // WA for aynschronous DB calls
    }

    @Test
    fun dummy() {
    }
}
