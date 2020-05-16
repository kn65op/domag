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
open class CategoriesPartTestSuite {

    @get:Rule
    var activityRule = ActivityTestRule(activityFactory, true, true)

    @Before
    fun fillDb() {
        val db = factory.createDatabase(ApplicationProvider.getApplicationContext())
        fillData(db)
        Thread.sleep(500) // WA for aynschronous DB calls
        goToCategories()
    }

    @After
    fun clearDb() {
        val db = factory.createDatabase(ApplicationProvider.getApplicationContext())
        db.clearAllTables()
        Thread.sleep(500) // WA for aynschronous DB calls
    }

    private fun goToCategories() {
        openPart("Categories")
    }

    private fun openAddCategory() {
        clickOnId(R.id.categories_add_category_fab)
    }

    private fun writeCategoryName(name: String) {
        typeNewTextOnId(R.id.edit_category_category_name, name)
    }

    private fun writeUnitName(unit: String) {
        typeNewTextOnId(R.id.edit_category_unit_field, unit)
    }

    private fun apply() {
        clickOnId(R.id.edit_depot_menu_confirm)
    }

    private fun assertCategoryInContents(name: String) {
        viewOrChildHasText(R.id.fragment_categories_layout, name)
    }

    private fun assertUnitInContents(name: String) {
        viewOrChildHasText(R.id.fragment_categories_layout, name)
    }

    private fun setParentCategory(name: String) {
        clickOnId(R.id.edit_category_fragment_parent_spinner)
        clickOnText(name)
    }

    private fun removeCategory() {
        clickEditCategory()
        clickOnId(R.id.edit_depot_menu_remove_depot_item)
    }

    private fun clickEditCategory() {
        clickOnId(R.id.categories_menu_edit_category_menu_item)
    }

    private fun addCategoryWithParent(parentName: String, name: String) {
        openAddCategory()
        writeCategoryName(name)
        setParentCategory(parentName)
        apply()
    }

    private fun changeUnit(categoryName: String, newUnit: String) {
        clickOnText(categoryName)
        clickEditCategory()
        writeUnitName(newUnit)
        apply()
    }

    private fun renameCategory(oldName: String, newName: String) {
        clickOnText(oldName)
        clickEditCategory()
        writeCategoryName(newName)
        apply()
    }

    private fun changeParent(name: String, parentName: String) {
        clickOnText(name)
        clickEditCategory()
        setParentCategory(parentName)
        apply()
    }

    private fun assertTitleIs(title: String) {
        viewOrChildHasText(R.id.toolbar, title)
    }

    private fun assertItemInContents(name: String, unit: String, amount: FixedPointNumber) {
        viewOrChildHasText(R.id.fragment_categories_layout, name)
        viewOrChildHasText(R.id.fragment_categories_layout, unit)
        viewOrChildHasText(R.id.fragment_categories_layout, amount.toString())
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
        assertCategoryInContents(mainCategory1Name)
        assertCategoryInContents(mainCategory1Unit)
    }

    @Test
    fun onClickCategoryShouldPrintItNameInBar() {
        clickOnText(mainCategory1Name)

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
    fun addedCategoryWithoutParentShouldBePrintedInTopLevelCategories() {
        val name = "New name"
        openAddCategory()

        writeCategoryName(name)
        apply()

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
    fun removeCategoryShouldRemoveItsChildren() {
        clickOnText(mainCategory1Name)

        removeCategory()

        //TODO: Search for removed childer
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

        assertItemInContents(category1InMainCategory1Name, category1InMainCategory1Unit, itemAmount2)
        assertItemInContents(category1InMainCategory1Name, category1InMainCategory1Unit, itemAmount4)
        assertItemInContents(category1InMainCategory1Name, category1InMainCategory1Unit, itemAmount5)
    }
}
