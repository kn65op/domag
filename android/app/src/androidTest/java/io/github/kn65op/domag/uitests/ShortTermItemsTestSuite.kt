package io.github.kn65op.domag.uitests

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.github.kn65op.domag.R
import io.github.kn65op.domag.application.modules.SqlDatabaseModule
import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.dbtests.data.*
import io.github.kn65op.domag.uitests.common.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@UninstallModules(SqlDatabaseModule::class)
@HiltAndroidTest
class ShortTermItemsTestSuite {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var activityRule = ActivityTestRule(activityFactory, true, true)

    @Inject
    lateinit var db: AppDatabase

    @Before
    fun prepareTestEnvironment() {
        injectObjects()
        fillDb()
        goToShortTerm()
    }

    private fun injectObjects() {
        hiltRule.inject()
    }

    private fun fillDb() {
        fillData(db)
        Thread.sleep(500) // WA for asynchronous DB calls
    }

    @After
    fun clearDb() {
        db.clearAllTables()
        Thread.sleep(500) // WA for asynchronous DB calls
    }

    private fun goToShortTerm() {
        openPart(R.id.nav_short_term)
    }

    private fun assertItemInContents(name: String) {
        viewHasChildWithText(R.id.short_term_items_recycler_view, name)
    }

    private fun assertNoItemInContents(name: String) {
        viewDoNotHasText(name)
    }

    private fun assertElementsCount(count: Int) {
        viewHasChildCount(R.id.short_term_items_recycler_view, count)
    }

    private fun descriptionWithCategoryAndDepot(
        description: String,
        category: String,
        depot: String
    ) = "$description ($category) in $depot"

    private fun categoryAndDepot(category: String, depot: String) = "$category in $depot"

    @Test
    fun shouldPrintShortTermItems() {
        assertElementsCount(5)
        assertItemInContents(
            descriptionWithCategoryAndDepot(
                item1Description,
                mainCategory1Name,
                mainDepot1Name
            )
        )
        assertItemInContents(
            descriptionWithCategoryAndDepot(
                item2Description,
                category1InMainCategory1Name,
                depot1InMainDepot1Name
            )
        )
        assertItemInContents(
            descriptionWithCategoryAndDepot(
                item3Description,
                mainCategory2Name,
                mainDepot2Name
            )
        )
        assertItemInContents(categoryAndDepot(category1InMainCategory1Name, depot1InMainDepot1Name))
        assertItemInContents(categoryAndDepot(category2InMainCategory1Name, depot2InMainDepot1Name))
    }

    @Test
    fun afterRemoveItemShouldNotBeShown() {
        val text = categoryAndDepot(category2InMainCategory1Name, depot2InMainDepot1Name)
        assertItemInContents(text)

        clickOnText(text)

        removeItem()

        assertNoItemInContents(text)
    }

    @Test
    fun afterChangeDateItemShouldNotBeShown() {
        val text = categoryAndDepot(category2InMainCategory1Name, depot2InMainDepot1Name)
        assertItemInContents(text)

        clickOnText(text)


        assertNoItemInContents(text)
    }
}
