package com.example.domag2.uitests.common

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.domag2.R
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

fun clickOnId(id: Int) {
    clickOn(Espresso.onView(ViewMatchers.withId(id)))
}

fun clickOnText(text: String) {
    clickOn(Espresso.onView(ViewMatchers.withText(text)))
}

fun typeNewTextOnId(id: Int, text: String) {
    typeNewTextOn(Espresso.onView(ViewMatchers.withId(id)), text)
}

fun clickHomeIcon() {
    //TODO: To be used in tests
    //onView(androidHomeMatcher()).perform(click())
    onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());

}

fun openPart(part : String) {
    onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
    onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_categories));
    sleep(500)
    /*
    // Open Drawer to click on navigation.
    onView(withId(R.id.drawer_layout))
        .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
        .perform(DrawerActions.open()); // Open Drawer

    // Start the screen of your activity.
    onView(withId(R.id.nav_view))
        .perform(NavigationViewActions.navigateTo(R.id.your_navigation_menu_item));

    // Check that you Activity was opened.
    String expectedNoStatisticsText = InstrumentationRegistry.getTargetContext()
        .getString(R.string.no_item_available);
    onView(withId(R.id.no_statistics)).check(matches(withText(expectedNoStatisticsText)));
    */
}

/*
Possible to be used
fun androidHomeMatcher(): org.hamcrest.Matcher<View>? {
    return allOf(
        withParent(withClassName(`is`(Toolbar::class.java.getName()))),
        withClassName(
            anyOf(
                `is`(ImageButton::class.java.getName()),
                `is`(AppCompatImageButton::class.java.getName())
            )
        )
    )
}*/
