package io.github.kn65op.domag.uitests

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import io.github.kn65op.domag.R
import io.github.kn65op.domag.dbtests.data.*
import io.github.kn65op.domag.uitests.common.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShortTermItemsTestSuite {
    @get:Rule
    var activityRule = ActivityTestRule(activityFactory, true, true)

    @Before
    fun fillDb() {
        val db = factory.createDatabase(ApplicationProvider.getApplicationContext())
        fillData(db)
        Thread.sleep(500) // WA for aynschronous DB calls
        goToShortTerm()
    }

    @After
    fun clearDb() {
        val db = factory.createDatabase(ApplicationProvider.getApplicationContext())
        db.clearAllTables()
        Thread.sleep(500) // WA for aynschronous DB calls
    }

    private fun goToShortTerm() {
        openPart(R.id.nav_short_term)
    }

    private fun assertItemInContents(name: String) {
        viewOrChildHasText(R.id.short_term_items_recycler_view, name)
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
