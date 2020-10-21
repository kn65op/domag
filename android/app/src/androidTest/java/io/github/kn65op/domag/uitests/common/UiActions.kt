package io.github.kn65op.domag.uitests.common

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.R
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.anyOf
import java.lang.Thread.sleep


private fun clickOn(view: ViewInteraction) {
    view.perform(click())
}

private fun typeNewTextOn(view: ViewInteraction, text: String) {
    view.perform(
        clearText(),
        typeText(text),
        closeSoftKeyboard()
    )
}


fun openActionBarMenu() {
    openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
}

fun clickOnId(id: Int) {
    clickOn(onView(withId(id)))
}

fun clickOnIdOrText(id: Int, text: Int) {
    onView(anyOf(withText(text), withId(id))).perform(click())
}

fun clickOnText(text: String) {
    clickOn(onView(withText(text)))
}

fun typeNewTextOnId(id: Int, text: String) {
    typeNewTextOn(onView(withId(id)), text)
}

fun removeItem() {
    openActionBarMenu()
    clickOnIdOrText(
        R.id.edit_item_menu_remove_item_item,
        R.string.edit_depot_menu_remove_depot_item_text
    )
}

fun openPart(id: Int) {
    onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
    onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(id))
    sleep(500)
}

fun openAddDepot() {
    clickOnId(R.id.items_general_fab)
    clickOnId(R.id.items_add_depot_fab)
}

fun openAddItem() {
    clickOnId(R.id.items_general_fab)
    clickOnId(R.id.items_add_item_fab)
}

fun openAddCategory() {
    clickOnId(R.id.items_general_fab)
    clickOnId(R.id.items_add_category_fab)
}

fun addDepot(name: String) {
    openAddDepot()
    writeDepotName(name)
    applyDepot()
}

fun writeDepotName(name: String) {
    typeNewTextOnId(R.id.edit_depot_depot_name, name)
}

fun applyDepot() {
    clickOnId(R.id.edit_depot_menu_confirm)
}

fun writeCategoryName(name: String) {
    typeNewTextOnId(R.id.edit_category_category_name, name)
}

fun setParentCategory(name: String) {
    clickOnId(R.id.edit_category_fragment_parent_spinner)
    clickOnText(name)
}

fun applyCategory() {
    clickOnId(R.id.edit_depot_menu_confirm)
}

fun addCategoryWithParent(parentName: String, name: String) {
    openAddCategory()
    writeCategoryName(name)
    setParentCategory(parentName)
    applyCategory()
}

fun addItem(amount: FixedPointNumber) {
    openAddItem()

    writeItemAmount(amount)

    applyItem()
}

fun writeItemAmount(amount: FixedPointNumber) {
    typeNewTextOnId(R.id.edit_item_amount_value, amount.toString())
}

fun applyItem() {
    clickOnId(R.id.edit_item_menu_confirm)
}

fun addItem(categoryName: String, amount: FixedPointNumber) {
    openAddItem()

    setItemCategory(categoryName)
    writeItemAmount(amount)

    applyItem()
}

fun setItemCategory(name: String) {
    clickOnId(R.id.edit_item_category_spinner)
    clickOnText(name)
}

fun setItemDepot(name: String) {
    clickOnId(R.id.edit_item_depot_spinner)
    clickOnText(name)
}

fun addItem(depotName: String, categoryName: String, amount: FixedPointNumber) {
    openAddItem()

    setItemCategory(categoryName)
    setItemDepot(depotName)
    writeItemAmount(amount)

    applyItem()
}

fun addItemWithDepotOnly(depotName: String, amount: FixedPointNumber) {
    openAddItem()

    setItemDepot(depotName)
    writeItemAmount(amount)

    applyItem()
}

fun findViewByIdInRow(description: String): ViewInteraction =
    onView(CoreMatchers.allOf(withId(R.id.item_row_consume_button), hasSibling(withText(description))))

fun consumeItem(itemDescription: String, amount :String) {
    clickOn(findViewByIdInRow(itemDescription))
    typeNewTextOnId(R.id.consume_dialog_amount_field, amount)
    clickOnText("ELOSZKA")
}
