package io.github.kn65op.domag.uitests.common

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.hamcrest.CoreMatchers

fun viewOrChildHasText(id: Int, text: String) {
    onView(withId(id)).check(matches(hasDescendant(withText(text))))
}

fun viewHasChildCount(id: Int, count: Int) {
    onView(withId(id)).check(matches(hasChildCount(count)))
}

fun viewDoNotHasText(text: String) {
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    val element = device.findObject(UiSelector().text(text))
    assertThat(element.exists(), CoreMatchers.equalTo(false))
}

fun dialogWithText(text : String) {
    onView(withText(text)).inRoot(RootMatchers.isDialog()).check(matches(isDisplayed()))
}

fun viewIdVisible(id: Int) {
    onView(withId(id)).check(matches(isDisplayed()))
}
