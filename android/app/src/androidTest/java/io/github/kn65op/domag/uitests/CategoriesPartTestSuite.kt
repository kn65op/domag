package io.github.kn65op.domag.uitests

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import io.github.kn65op.domag.R
import io.github.kn65op.domag.dbtests.data.*
import io.github.kn65op.domag.uitests.common.*
import io.github.kn65op.android.lib.type.FixedPointNumber
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
open class CategoriesPartTestSuite {

    @get:Rule
    val activityRule = ActivityTestRule(activityFactory, true, true)

    @Before
    fun fillDb() {
        val db = factory.createDatabase(ApplicationProvider.getApplicationContext())
        fillData(db)
        Thread.sleep(500) // WA for asynchronous DB calls
        goToCategories()
    }

    @After
    fun clearDb() {
        val db = factory.createDatabase(ApplicationProvider.getApplicationContext())
        db.clearAllTables()
        Thread.sleep(500) // WA for asynchronous DB calls
    }

    private fun goToCategories() {
        openPart(R.id.nav_categories)
    }

    private fun writeUnitName(unit: String) {
        typeNewTextOnId(R.id.edit_category_unit_field, unit)
    }

    private fun assertCategoryInContents(name: String) {
        viewHasChildWithText(R.id.fragment_categories_layout, name)
    }

    private fun assertUnitInContents(name: String) {
        viewHasChildWithText(R.id.fragment_categories_layout, name)
    }

    private fun removeCategory() {
        clickEditCategory()
        clickOnId(R.id.edit_depot_menu_remove_depot_item)
    }

    private fun clickEditCategory() {
        clickOnId(R.id.categories_menu_edit_category_menu_item)
    }

    private fun changeUnit(categoryName: String, newUnit: String) {
        clickOnText(categoryName)
        clickEditCategory()
        writeUnitName(newUnit)
        applyCategory()
    }

    private fun renameCategory(oldName: String, newName: String) {
        clickOnText(oldName)
        clickEditCategory()
        writeCategoryName(newName)
        applyCategory()
    }

    private fun changeLimit(name: String, newLimit: FixedPointNumber) {
        clickOnText(name)
        clickEditCategory()
        writeCategoryLimit(newLimit)
        applyCategory()
    }

    private fun removeLimit(name: String) {
        clickOnText(name)
        clickEditCategory()
        writeNoCategoryLimit()
        applyCategory()
    }

    private fun changeParent(name: String, parentName: String) {
        clickOnText(name)
        clickEditCategory()
        setParentCategory(parentName)
        applyCategory()
    }

    private fun assertTitleIs(title: String) {
        viewHasChildWithText(R.id.toolbar, title)
    }

    private fun assertItemInContents(name: String, unit: String, amount: FixedPointNumber) {
        viewHasChildWithText(R.id.fragment_categories_layout, name)
        viewHasChildWithText(R.id.fragment_categories_layout, unit)
        viewHasChildWithText(R.id.fragment_categories_layout, amount.toString())
    }

    private fun validateLimit(limit: FixedPointNumber, unit: String) {
        viewHasText(
            R.id.fragment_categories_limit_info,
            "Configured minimum amount: $limit $unit"
        )
    }

    private fun validateNoLimitText() {
        noViewVisible(R.id.fragment_categories_limit_info)
    }

    private fun validateLimitTextVisible() {
        viewIdVisible(R.id.fragment_categories_limit_info)
    }

    @Test
    fun onStartShouldPrintCategories() {
        val title = "Categories"
        assertTitleIs(title)
    }

    @Test
    fun onStartShouldPrintTopLevelCategories() {
        assertCategoryInContents(mainCategory1Name)
        assertCategoryInContents(mainCategory2Name)
    }

    @Test
    fun onClickCategoryShouldPrintAnotherCategoryContents() {
        clickOnText(mainCategory1Name)

        assertCategoryInContents(category1InMainCategory1Name)
        assertCategoryInContents(category2InMainCategory1Name)
        assertCategoryInContents(mainCategory1Unit)
    }

    @Test
    fun onClickCategoryShouldPrintItNameInBar() {
        clickOnText(mainCategory1Name)

        assertTitleIs(mainCategory1Name)
    }

    @Test
    fun onClickEditCategoryShouldPrintItNameInBar() {
        clickOnText(mainCategory1Name)
        clickEditCategory()

        assertTitleIs(mainCategory1Name)
    }

    @Test
    fun goBackShouldPrintCategoriesInTitle() {
        clickOnText(mainCategory1Name)
        Espresso.pressBack()

        assertTitleIs("Categories")
    }

    @Test
    fun goBackShouldGoToParentCategory() {
        clickOnText(mainCategory1Name)
        Espresso.pressBack()

        assertCategoryInContents(mainCategory2Name)
    }

    @Test
    fun openAddCategoryShouldHaveNewCategoryTitle() {
        val name = "New category"
        openAddCategory()

        assertTitleIs(name)
    }

    @Test
    fun addedCategoryWithoutParentShouldBePrintedInTopLevelCategories() {
        val name = "New name"
        openAddCategory()

        writeCategoryName(name)
        applyCategory()

        assertCategoryInContents(name)
    }

    @Test
    fun addCategoryWithParentShouldNotBePrinterInTopLevelCategories() {
        val name = "Some different"

        addCategoryWithParent(mainCategory1Name, name)

        viewDoNotHasText(name)
    }

    @Test
    fun addCategoryWithParentShouldBePrinterUnderParent() {
        val name = "New name"

        addCategoryWithParent(mainCategory1Name, name)

        clickOnText(mainCategory1Name)

        assertCategoryInContents(name)
    }

    @Test
    fun removeCategoryShouldNotBePrinted() {
        clickOnText(mainCategory1Name)

        removeCategory()

        viewDoNotHasText(mainCategory1Name)
    }

    @Test
    fun removeCategoryShouldNotBePrintedAndGoToParent() {
        clickOnText(mainCategory1Name)
        clickOnText(category1InMainCategory1Name)

        removeCategory()

        viewDoNotHasText(category1InMainCategory1Name)
        assertTitleIs(mainCategory1Name)
    }

    @Test
    fun renameCategoryShouldRenameTitle() {
        val newName = "NewName"
        renameCategory(mainCategory1Name, newName)

        assertTitleIs(newName)
    }

    @Test
    fun changeUnitShouldRenameUnit() {
        val newUnit = "NewUnit"
        changeUnit(mainCategory1Name, newUnit)

        assertUnitInContents(newUnit)
    }

    @Test
    fun renameCategoryShouldRenameOnList() {
        val newName = "Renamed"
        renameCategory(mainCategory1Name, newName)

        Espresso.pressBack()

        assertCategoryInContents(newName)
    }

    @Test
    fun changeParent() {
        clickOnText(mainCategory1Name)

        changeParent(category1InMainCategory1Name, mainCategory2Name)

        Espresso.pressBack()

        assertTitleIs(mainCategory2Name)
    }

    @Test
    fun shouldPrintItem() {
        clickOnText(mainCategory1Name)
        clickOnText(category1InMainCategory1Name)

        assertItemInContents(
            "$item2Description$descriptionCategoryDelimiter$depot1InMainDepot1Name",
            category1InMainCategory1Unit,
            itemAmount2
        )
        assertItemInContents(mainDepot1Name, category1InMainCategory1Unit, itemAmount4)
        assertItemInContents(depot1InMainDepot1Name, category1InMainCategory1Unit, itemAmount5)
    }

    @Test
    fun addDepotAndItemFromCategoriesList() {
        clickOnText(mainCategory1Name)

        val newDepotName = "New depot"
        val amount = FixedPointNumber(2.34)
        addDepot(newDepotName)
        addItem(depotName = newDepotName, categoryName = mainCategory1Name, amount = amount)

        assertItemInContents(name = newDepotName, unit = mainCategory1Unit, amount = amount)
    }

    @Test
    fun addDepotAndItemFromCategoriesListWithDefaultCategory() {
        clickOnText(mainCategory2Name)

        val newDepotName = "New depot"
        val amount = FixedPointNumber(2.34)
        addDepot(newDepotName)
        addItemWithDepotOnly(depotName = newDepotName, amount = amount)

        assertItemInContents(name = newDepotName, unit = mainCategory2Unit, amount = amount)
    }

    @Test
    fun whenChangeLimitItShouldBeChanged() {
        val newLimit = FixedPointNumber(1.50)
        changeLimit(mainCategory2Name, newLimit)

        validateLimit(newLimit, mainCategory2Unit)
        validateLimitTextVisible()
    }

    @Test
    fun whenCreateLimitItShouldBeChanged() {
        val newLimit = FixedPointNumber(0.01)
        changeLimit(mainCategory1Name, newLimit)

        validateLimit(newLimit, mainCategory1Unit)
    }

    @Test
    fun whenThereIsNoLimitShouldNotPrintLimit() {
        clickOnText(mainCategory2Name)

        validateNoLimitText()
    }

    @Test
    fun whenDeleteLimitShouldBeNotVisible() {
        removeLimit(mainCategory1Name)

        validateNoLimitText()
    }
}
