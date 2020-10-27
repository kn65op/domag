package io.github.kn65op.domag.uitests

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.R
import io.github.kn65op.domag.dbtests.data.*
import io.github.kn65op.domag.uitests.common.activityFactory
import io.github.kn65op.domag.uitests.common.factory
import io.github.kn65op.domag.uitests.common.openPart
import io.github.kn65op.domag.uitests.common.viewHasChildWithText
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShortagePartTestSuite {
    @get:Rule
    var activityRule = ActivityTestRule(activityFactory, true, true)

    @Before
    fun fillDb() {
        val db = factory.createDatabase(ApplicationProvider.getApplicationContext())
        fillData(db)
        Thread.sleep(500) // WA for asynchronous DB calls
        goToShortage()
    }

    @After
    fun clearDb() {
        val db = factory.createDatabase(ApplicationProvider.getApplicationContext())
        db.clearAllTables()
        Thread.sleep(500) // WA for asynchronous DB calls
    }

    private fun goToShortage() {
        openPart(R.id.nav_shortage)
    }

    private fun assertCategoryInContents(
        name: String,
        amount: FixedPointNumber,
        limit: FixedPointNumber
    ) {
        viewHasChildWithText(R.id.fragment_shortage_recycler_view, name)
        viewHasChildWithText(R.id.fragment_shortage_recycler_view, "$amount/$limit")
    }

    @Test
    fun shouldShowShortageItems() {
        assertCategoryInContents(
            mainCategory1Name,
            mainCategory1ItemsAmountCount,
            mainCategory1LimitAmount
        )
    }

    @Test
    fun shouldShowShortageItemsForCategoryWithoutItems() {
        assertCategoryInContents(
            mainCategory3Name,
            mainCategory3ItemsAmountCount,
            someCategoryLimit
        )
    }
}
