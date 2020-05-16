package com.example.domag2.uitests

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.domag2.R
import com.example.domag2.dbtests.data.*
import com.example.domag2.uitests.common.*
import io.github.kn65op.android.lib.type.FixedPointNumber
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
open class ItemsPartTestSuite {

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

    private fun openAddDepot() {
        clickOnId(R.id.items_general_fab)
        clickOnId(R.id.items_add_depot_fab)
    }

    private fun writeDepotName(name: String) {
        typeNewTextOnId(R.id.edit_depot_depot_name, name)
    }

    private fun apply() {
        clickOnId(R.id.edit_depot_menu_confirm)
    }

    private fun assertDepotInContents(name: String) {
        viewOrChildHasText(R.id.fragment_items_layout, name)
    }

    private fun assertItemInContents(name: String, unit: String, amount: FixedPointNumber) {
        viewOrChildHasText(R.id.fragment_items_layout, name)
        viewOrChildHasText(R.id.fragment_items_layout, unit)
        viewOrChildHasText(R.id.fragment_items_layout, amount.toString())
    }

    private fun setParentDepot(name: String) {
        clickOnId(R.id.edit_depot_fragment_parent_spinner)
        clickOnText(name)
    }

    private fun removeDepot() {
        clickEditDepot()
        clickOnId(R.id.edit_depot_menu_remove_depot_item)
    }

    private fun clickEditDepot() {
        clickOnId(R.id.items_edit_depot_menu_item)
    }

    private fun addDepotWithParent(parentName: String, name: String) {
        openAddDepot()
        writeDepotName(name)
        setParentDepot(parentName)
        apply()
    }

    private fun renameDepot(oldName: String, newName: String) {
        clickOnText(oldName)
        clickEditDepot()
        writeDepotName(newName)
        apply()
    }

    private fun changeParent(name: String, parentName: String) {
        clickOnText(name)
        clickEditDepot()
        setParentDepot(parentName)
        apply()
    }

    private fun asserTitleIs(title: String) {
        viewOrChildHasText(R.id.toolbar, title)
    }

    private fun openAddItem() {
        clickOnId(R.id.items_general_fab)
        clickOnId(R.id.items_add_item_fab)
    }

    private fun addItem(categoryName: String, amount: FixedPointNumber) {
        openAddItem()

        setCategory(categoryName)
        writeItemAmount(amount)

        apply()
    }

    private fun writeItemAmount(amount: FixedPointNumber) {
        typeNewTextOnId(R.id.edit_item_amount_value, amount.toString())
    }

    private fun setCategory(name: String) {
        clickOnId(R.id.edit_item_category_spinner)
        clickOnText(name)
    }

    private fun assertEmptyAmountDialog() {
        dialogWithText("Amount can't be empty")
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
        assertDepotInContents(mainCategory1Name)
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

        assertItemInContents(mainCategory1Name, mainCategory1Unit, itemAmount1)
        assertItemInContents(
            category1InMainCategory1Name,
            category1InMainCategory1Unit,
            itemAmount4
        )
    }

    @Test
    fun addedItemShouldBeShown() {
        clickOnText(mainDepot1Name)

        val itemAmount = FixedPointNumber(8)

        addItem(mainCategory2Name, itemAmount)

        assertItemInContents(mainCategory2Name, mainCategory2Unit, itemAmount)
    }

    @Test fun shouldNotAllowAddItemWithEmptyAmount() {
        clickOnText(mainDepot1Name)

        openAddItem()
        apply()

        assertEmptyAmountDialog()

        clickOnText("OK")
    }
}
