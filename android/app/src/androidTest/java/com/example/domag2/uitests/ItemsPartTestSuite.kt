package com.example.domag2.uitests

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.domag2.R
import com.example.domag2.dbtests.data.*
import com.example.domag2.uitests.common.*
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import io.github.kn65op.android.lib.type.FixedPointNumber
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

open class ItemsPartBase {
    @get:Rule
    var activityRule = ActivityTestRule(activityFactory, true, true)

    @After
    fun clearDb() {
        val db = factory.createDatabase(ApplicationProvider.getApplicationContext())
        db.clearAllTables()
        Thread.sleep(500) // WA for aynschronous DB calls
    }

    internal fun openAddDepot() {
        clickOnId(R.id.items_general_fab)
        clickOnId(R.id.items_add_depot_fab)
    }

    internal fun writeDepotName(name: String) {
        typeNewTextOnId(R.id.edit_depot_depot_name, name)
    }

    internal fun apply() {
        clickOnId(R.id.edit_depot_menu_confirm)
    }

    internal fun assertDepotInContents(name: String) {
        viewOrChildHasText(R.id.fragment_items_layout, name)
    }

    internal fun assertItemInContents(name: String, unit: String, amount: FixedPointNumber) {
        viewOrChildHasText(R.id.fragment_items_layout, name)
        viewOrChildHasText(R.id.fragment_items_layout, unit)
        viewOrChildHasText(R.id.fragment_items_layout, amount.toString())
    }

    internal fun assertDepotContentSize(size: Int) {
        viewHasChildCount(R.id.items_recycler_view, size)
    }

    private fun setParentDepot(name: String) {
        clickOnId(R.id.edit_depot_fragment_parent_spinner)
        clickOnText(name)
    }

    internal fun removeDepot() {
        clickEditDepot()
        clickOnId(R.id.edit_depot_menu_remove_depot_item)
    }

    private fun clickEditDepot() {
        clickOnId(R.id.items_edit_depot_menu_item)
    }

    internal fun addDepot(name: String) {
        openAddDepot()
        writeDepotName(name)
        apply()
    }

    internal fun addDepotWithParent(parentName: String, name: String) {
        openAddDepot()
        writeDepotName(name)
        setParentDepot(parentName)
        apply()
    }

    internal fun renameDepot(oldName: String, newName: String) {
        clickOnText(oldName)
        clickEditDepot()
        writeDepotName(newName)
        apply()
    }

    internal fun changeParent(name: String, parentName: String) {
        clickOnText(name)
        clickEditDepot()
        setParentDepot(parentName)
        apply()
    }

    internal fun asserTitleIs(title: String) {
        viewOrChildHasText(R.id.toolbar, title)
    }

    internal fun openAddItem() {
        clickOnId(R.id.items_general_fab)
        clickOnId(R.id.items_add_item_fab)
    }

    internal fun addItem(amount: FixedPointNumber) {
        openAddItem()

        writeItemAmount(amount)

        apply()
    }

    internal fun addItem(categoryName: String, amount: FixedPointNumber) {
        openAddItem()

        setCategory(categoryName)
        writeItemAmount(amount)

        apply()
    }

    internal fun writeItemAmount(amount: FixedPointNumber) {
        typeNewTextOnId(R.id.edit_item_amount_value, amount.toString())
    }

    internal fun writeDescription(description: String) {
        typeNewTextOnId(R.id.edit_item_description, description)
    }

    internal fun setCategory(name: String) {
        clickOnId(R.id.edit_item_category_spinner)
        clickOnText(name)
    }

    internal fun assertEmptyAmountDialog() {
        dialogWithText("Amount can't be empty.")
    }

    internal fun assertEmptyCategoryDialog() {
        dialogWithText("Category can't be empty.")
    }

    internal fun assertEmptyDepotDialog() {
        dialogWithText("Depot can't be empty.")
    }
}

@RunWith(AndroidJUnit4::class)
open class ItemsPartWithEmptyDbTestSuite : ItemsPartBase() {
    @Test
    fun WhenNoDepotShouldNotAddItem() {
        openAddItem()
        writeItemAmount(FixedPointNumber(1))
        apply()

        assertEmptyDepotDialog()

        clickOnText("OK")
    }

    @Test
    fun WhenNoCategoryShouldNotAddItem() {
        addDepot(mainDepot1Name)

        openAddItem()
        writeItemAmount(FixedPointNumber(1))
        apply()

        assertEmptyCategoryDialog()

        clickOnText("OK")
    }

}


@RunWith(AndroidJUnit4::class)
open class ItemsPartTestSuite : ItemsPartBase() {

    @Before
    fun fillDb() {
        val db = factory.createDatabase(ApplicationProvider.getApplicationContext())
        fillData(db)
        Thread.sleep(500) // WA for aynschronous DB calls
    }

    @Test
    fun onStartShouldPrintItems() {
        val title = "Items"
        asserTitleIs(title)
    }

    @Test
    fun onStartShouldPrintTopLevelDepots() {
        assertDepotInContents(mainDepot1Name)
        assertDepotInContents(mainDepot2Name)
    }

    @Test
    fun onClickDepotShouldPrintAnotherDepotContents() {
        clickOnText(mainDepot1Name)

        assertDepotInContents(depot1InMainDepot1Name)
        assertDepotInContents(depot2InMainDepot1Name)
        assertDepotInContents(mainCategory1Unit)
    }

    @Test
    fun onClickDepotShouldPrintItNameInBar() {
        clickOnText(mainDepot1Name)

        asserTitleIs(mainDepot1Name)
    }

    @Test
    fun goBackShouldPrintItemsInTitle() {
        clickOnText(mainDepot1Name)
        Espresso.pressBack()

        asserTitleIs("Items")
    }

    @Test
    fun goBackShouldGoToParentDepot() {
        clickOnText(mainDepot1Name)
        Espresso.pressBack()

        assertDepotInContents(mainDepot2Name)
    }

    @Test
    fun addedDepotWithoutParentShouldBePrintedInTopLevelDepots() {
        val name = "New name"
        openAddDepot()

        writeDepotName(name)
        apply()

        assertDepotInContents(name)
    }

    @Test
    fun addDepotWithParentShouldNotBePrinterInTopLevelDepots() {
        val name = "Some different"

        addDepotWithParent(mainDepot1Name, name)

        viewDoNotHasText(name)
    }

    @Test
    fun addDepotWithParentShouldBePrinterUnderParent() {
        val name = "New name"

        addDepotWithParent(mainDepot1Name, name)

        clickOnText(mainDepot1Name)

        assertDepotInContents(name)
    }

    @Test
    fun removeDepotShouldNotBePrinted() {
        clickOnText(mainDepot1Name)

        removeDepot()

        viewDoNotHasText(mainDepot1Name)
    }

    @Test
    fun removeDepotShouldRemoveItsChildren() {
        clickOnText(mainDepot1Name)

        removeDepot()

        //TODO: Search for removed childer
    }

    @Test
    fun removeDepotShouldNotBePrintedAndGoToParent() {
        clickOnText(mainDepot1Name)
        clickOnText(depot1InMainDepot1Name)

        removeDepot()

        viewDoNotHasText(depot1InMainDepot1Name)
        asserTitleIs(mainDepot1Name)
    }

    @Test
    fun renameDepotShouldRenameTitle() {
        val newName = "NewName"
        renameDepot(mainDepot1Name, newName)

        asserTitleIs(newName)
    }

    @Test
    fun renameDepotShouldRenameOnList() {
        val newName = "Renamed"
        renameDepot(mainDepot1Name, newName)

        Espresso.pressBack()

        assertDepotInContents(newName)
    }

    @Test
    fun changeParent() {
        clickOnText(mainDepot1Name)

        changeParent(depot1InMainDepot1Name, mainDepot2Name)

        Espresso.pressBack()

        asserTitleIs(mainDepot2Name)
    }

    @Test
    fun shouldPrintItemInContainer() {
        clickOnText(mainDepot1Name)

        assertItemInContents(
            "$item1Description$descriptionCategoryDelimiter$mainCategory1Name",
            mainCategory1Unit,
            itemAmount1
        )
        assertItemInContents(
            category1InMainCategory1Name,
            category1InMainCategory1Unit,
            itemAmount4
        )
    }

    @Test
    fun addItemTitleShouldBeCategory() {
        openAddItem()

        val someCategory = category2InMainCategory1Name

        setCategory(someCategory)

        asserTitleIs(someCategory)
    }

    @Test
    fun addItemTitleShouldBeDescriptionAndCategory() {
        openAddItem()

        val someCategory = mainCategory2Name
        val description = "FINE"

        setCategory(someCategory)
        writeDescription(description)

        asserTitleIs("$description$descriptionCategoryDelimiter$someCategory")
    }

    @Test
    fun addItemTitleShouldBeDescriptionAndCategoryWhenChangeCategoryLater() {
        openAddItem()

        val someCategory = mainCategory2Name
        val description = "FINE"

        writeDescription(description)
        setCategory(someCategory)

        asserTitleIs("$description$descriptionCategoryDelimiter$someCategory")
    }

    @Test
    fun addedItemShouldBeShown() {
        clickOnText(mainDepot1Name)

        val itemAmount = FixedPointNumber(8)

        addItem(mainCategory2Name, itemAmount)

        assertItemInContents(mainCategory2Name, mainCategory2Unit, itemAmount)
    }

    @Test
    fun shouldNotAllowAddItemWithEmptyAmount() {
        clickOnText(mainDepot1Name)

        openAddItem()
        apply()

        assertEmptyAmountDialog()

        clickOnText("OK")
    }

    @Test
    fun whenAddItemShouldAddItemInDepotWhereAddItemWasClicked() {
        clickOnText(mainDepot2Name)

        val amount = FixedPointNumber(1.1)
        addItem(amount)

        assertItemInContents(
            "$mainCategory1Name",
            mainCategory1Unit,
            amount
        )
    }

    @Test
    fun ediItem() {
        clickOnText(mainDepot1Name)
        clickOnText("$item1Description$descriptionCategoryDelimiter$mainCategory1Name")

        val amount = FixedPointNumber(3.4)
        writeItemAmount(amount)

        apply()

        assertItemInContents(
            "$item1Description$descriptionCategoryDelimiter$mainCategory1Name",
            mainCategory1Unit,
            amount
        )

        assertDepotContentSize(4)
    }

    @Test
    fun ediItemWithoutChangesShouldWriteSameItem() {
        clickOnText(mainDepot1Name)
        clickOnText("$item1Description$descriptionCategoryDelimiter$mainCategory1Name")

        apply()

        assertItemInContents(
            "$item1Description$descriptionCategoryDelimiter$mainCategory1Name",
            mainCategory1Unit,
            itemAmount1
        )

        assertDepotContentSize(4)
    }

    @Test
    fun ediItemWithoutChangesShouldWriteItemInTheSameDepot() {
        clickOnText(mainDepot2Name)
        clickOnText("$item3Description$descriptionCategoryDelimiter$mainCategory2Name")

        apply()

        assertItemInContents(
            "$item3Description$descriptionCategoryDelimiter$mainCategory2Name",
            mainCategory2Unit,
            itemAmount3
        )

        assertDepotContentSize(2)
    }
}
