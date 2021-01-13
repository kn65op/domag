package io.github.kn65op.domag.uitests

import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.github.kn65op.android.lib.type.FixedPointNumber
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

open class ItemsPartBase {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var activityRule = ActivityTestRule(activityFactory, true, true)

    protected val item1FullDescription =
        "$item1Description$descriptionCategoryDelimiter$mainCategory1Name"

    internal fun assertDepotInContents(name: String) {
        viewHasChildWithText(R.id.fragment_items_layout, name)
    }

    internal fun assertItemInContents(name: String, unit: String, amount: FixedPointNumber) {
        viewHasChildWithText(R.id.fragment_items_layout, name)
        viewHasChildWithText(R.id.fragment_items_layout, unit)
        viewHasChildWithText(R.id.fragment_items_layout, amount.toString())
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

    internal fun addDepotWithParent(parentName: String, name: String) {
        openAddDepot()
        writeDepotName(name)
        setParentDepot(parentName)
        applyDepot()
    }

    internal fun renameDepot(oldName: String, newName: String) {
        clickOnText(oldName)
        clickEditDepot()
        writeDepotName(newName)
        applyDepot()
    }

    internal fun changeParent(name: String, parentName: String) {
        clickOnText(name)
        clickEditDepot()
        setParentDepot(parentName)
        applyDepot()
    }

    internal fun asserTitleIs(title: String) {
        viewHasChildWithText(R.id.toolbar, title)
    }

    internal fun writeDescription(description: String) {
        typeNewTextOnId(R.id.edit_item_description, description)
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

    fun removeItem(itemName: String) {
        clickOnText(itemName)
        removeItem()
    }

}

@RunWith(AndroidJUnit4::class)
@UninstallModules(SqlDatabaseModule::class)
@HiltAndroidTest
open class ItemsPartWithEmptyDbTestSuite : ItemsPartBase() {
    @Test
    fun whenNoDepotShouldNotAddItem() {
        openAddItem()
        writeItemAmount(FixedPointNumber(1))
        applyItem()

        assertEmptyDepotDialog()

        clickOnText("OK")
    }

    @Test
    fun whenNoCategoryShouldNotAddItem() {
        addDepot(mainDepot1Name)

        openAddItem()
        writeItemAmount(FixedPointNumber(1))
        applyItem()

        assertEmptyCategoryDialog()

        clickOnText("OK")
    }

}


@RunWith(AndroidJUnit4::class)
@UninstallModules(SqlDatabaseModule::class)
@HiltAndroidTest
open class ItemsPartTestSuite : ItemsPartBase() {
    @Inject
    lateinit var db: AppDatabase

    @Before
    fun prepareTestEnvironment() {
        injectObjects()
        fillDb()
    }

    fun injectObjects() {
        hiltRule.inject()
    }

    fun fillDb() {
        fillData(db)
        Thread.sleep(500) // WA for asynchronous DB calls
    }

    @After
    fun clearDb() {
        db.clearAllTables()
        Thread.sleep(500) // WA for asynchronous DB calls
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
        applyDepot()

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

        setItemCategory(someCategory)

        asserTitleIs(someCategory)
    }

    @Test
    fun addItemTitleShouldBeDescriptionAndCategory() {
        openAddItem()

        val someCategory = mainCategory2Name
        val description = "FINE"

        setItemCategory(someCategory)
        writeDescription(description)

        asserTitleIs("$description$descriptionCategoryDelimiter$someCategory")
    }

    @Test
    fun addItemTitleShouldBeDescriptionAndCategoryWhenChangeCategoryLater() {
        openAddItem()

        val someCategory = mainCategory2Name
        val description = "FINE"

        writeDescription(description)
        setItemCategory(someCategory)

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
        emptyItemAmount()
        applyItem()

        assertEmptyAmountDialog()

        clickOnText("OK")
    }

    @Test
    fun whenAddItemShouldAddItemInDepotWhereAddItemWasClicked() {
        clickOnText(mainDepot2Name)

        val amount = FixedPointNumber(1.1)
        addItem(amount)

        assertItemInContents(
            mainCategory1Name,
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

        applyItem()

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
        clickOnText(item1WholeNameWithCategory)

        applyItem()

        assertItemInContents(
            item1WholeNameWithCategory,
            mainCategory1Unit,
            itemAmount1
        )

        assertDepotContentSize(4)
    }

    @Test
    fun ediItemWithoutChangesShouldWriteItemInTheSameDepot() {
        clickOnText(mainDepot2Name)
        clickOnText(item3WholeNameWithCategory)

        applyItem()

        assertItemInContents(
            item3WholeNameWithCategory,
            mainCategory2Unit,
            itemAmount3
        )

        assertDepotContentSize(2)
    }

    @Test
    fun removeItemShouldRemoveItem() {
        clickOnText(mainDepot1Name)
        assertDepotContentSize(4)

        removeItem(item1WholeNameWithCategory)

        assertDepotContentSize(3)
    }

    @Test
    fun moveContainerToAnotherContainer() {
        clickOnText(mainDepot1Name)

        changeParent(depot1InMainDepot1Name, depot2InMainDepot1Name)

        Espresso.pressBack()

        assertDepotInContents(depot1InMainDepot1Name)
        asserTitleIs(depot2InMainDepot1Name)
    }

    @Test
    fun addCategoryFromItemsList() {
        val newCategoryName = "New category"
        val amount = FixedPointNumber(2.2)

        addCategoryWithParent("(No parent)", newCategoryName)

        clickOnText(mainDepot1Name)
        addItem(newCategoryName, amount)

        assertItemInContents(newCategoryName, "", amount)
    }

    @Test
    fun consumeItem() {
        clickOnText(mainDepot1Name)

        consumeItem(item1FullDescription, "0.5")

        assertItemInContents(item1FullDescription, mainCategory1Unit, FixedPointNumber(0.5))
    }

    @Test
    fun consumeWholeItem() {
        clickOnText(mainDepot1Name)
        assertDepotContentSize(4)

        consumeItem(item1FullDescription, "1.0")

        assertDepotContentSize(3)
    }

    @Test
    fun consumeMoreThenAvailableShouldKeepDialog() {
        clickOnText(mainDepot1Name)

        consumeItem(item1FullDescription, "1.01")

        viewIdVisible(R.id.consume_dialog_description)
    }
}
