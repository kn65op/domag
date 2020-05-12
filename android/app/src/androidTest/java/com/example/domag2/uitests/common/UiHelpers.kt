package com.example.domag2.uitests.common

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.hamcrest.CoreMatchers

fun viewOrChildHasText(id: Int, text: String) {
    onView(withId(id)).check(matches(hasDescendant(withText(text))))
}

fun viewDoNotHasText(text: String) {
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    val element = device.findObject(UiSelector().text(text))
    assertThat(element.exists(), CoreMatchers.equalTo(false))
}